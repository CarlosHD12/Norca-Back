package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.Detalle_VentDTO;
import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.DTO.TallaDTO;
import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.*;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.Detalle_VentService;
import com.upc.ep.Services.PrendaService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Detalle_VentIMPL implements Detalle_VentService {
    @Autowired
    private Detalle_VentRepos dvRepos;

    @Autowired
    private VentaRepos ventaRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private LoteRepos loteRepos;

    @Autowired
    private MetricaVentaRepos metricaRepos;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Detalle_VentRepos detalle_VentRepos;

    @Override
    @Transactional
    public Detalle_VentDTO saveDetalleVenta(Detalle_VentDTO dto) {
        Detalle_Vent detalle = new Detalle_Vent();

        // ------------------- Datos básicos -------------------
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setSubTotal(dto.getSubTotal());

        // ------------------- Prenda -------------------
        Prenda prenda = null;
        if (dto.getPrenda() != null && dto.getPrenda().getIdPrenda() != null) {
            prenda = prendaRepos.findById(dto.getPrenda().getIdPrenda())
                    .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
            detalle.setPrenda(prenda);
        }

        // ------------------- Venta -------------------
        Venta venta = null;
        if (dto.getVenta() != null && dto.getVenta().getIdVenta() != null) {
            venta = ventaRepos.findById(dto.getVenta().getIdVenta())
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
            detalle.setVenta(venta);
        }

        // ------------------- Talla y stock -------------------
        Talla talla = null;
        if (dto.getTalla() != null && dto.getTalla().getIdTalla() != null) {
            talla = tallaRepos.findById(dto.getTalla().getIdTalla())
                    .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

            if (talla.getCantidad() < dto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para la talla " + talla.getSize());
            }

            // Reducir stock de la talla
            talla.setCantidad(talla.getCantidad() - dto.getCantidad());
            tallaRepos.save(talla);

            detalle.setTalla(talla);

            // Actualizar stock total de la prenda de manera segura
            assert prenda != null;
            int nuevoStock = prenda.getTallas() != null
                    ? prenda.getTallas().stream().mapToInt(Talla::getCantidad).sum()
                    : 0;

            prenda.setStock(nuevoStock);
            prendaRepos.save(prenda);

            // Actualizar estado de la prenda según stock
            prendaService.actualizarEstadoPrenda(prenda);
        }


        // ------------------- Guardar detalle -------------------
        Detalle_Vent saved = dvRepos.save(detalle);
        registrarMetricaVenta(prenda, venta, dto.getCantidad(), dto.getPrecioUnitario(), venta.getFechahoraVenta());

        // ------------------- Actualizar total de venta -------------------
        if (venta != null) {
            Double nuevoTotal = dvRepos.sumTotalByVenta(venta.getIdVenta());
            venta.setTotal(nuevoTotal != null ? nuevoTotal : 0.0);
            ventaRepos.save(venta);
        }

        // ------------------- Convertir a DTO seguro -------------------
        Detalle_VentDTO response = new Detalle_VentDTO();
        response.setIdDV(saved.getIdDV());
        response.setCantidad(saved.getCantidad());
        response.setPrecioUnitario(saved.getPrecioUnitario());
        response.setSubTotal(saved.getSubTotal());
        response.setPrenda(saved.getPrenda() != null ? modelMapper.map(saved.getPrenda(), PrendaDTO.class) : null);
        response.setVenta(saved.getVenta() != null ? modelMapper.map(saved.getVenta(), VentaDTO.class) : null);
        response.setTalla(saved.getTalla() != null ? modelMapper.map(saved.getTalla(), TallaDTO.class) : null);

        return response;
    }

    private void registrarMetricaVenta(Prenda prenda, Venta venta, int cantidad, double precioVenta, LocalDateTime fechaVenta) {
        // Obtener costo unitario del último lote
        List<Lote> lotes = loteRepos.findByPrendaIdOrderByFechaIngresoDesc(prenda.getIdPrenda());
        double costoUnitario = 0.0;
        if (!lotes.isEmpty()) {
            Lote ultimoLote = lotes.get(0);
            costoUnitario = ultimoLote.getCantidad() > 0
                    ? ultimoLote.getPrecioCompraTotal() / ultimoLote.getCantidad()
                    : 0.0;
        }

        // Calcular ganancia
        double ganancia = (precioVenta - costoUnitario) * cantidad;

        // Guardar métrica
        MetricaVenta metrica = new MetricaVenta();
        metrica.setPrendaId(prenda.getIdPrenda());
        metrica.setVentaId(venta.getIdVenta());
        metrica.setUnidadesVendidas(cantidad);
        metrica.setIngresos(precioVenta * cantidad);
        metrica.setGanancia(ganancia);
        metrica.setFechaVenta(fechaVenta);

        metricaRepos.save(metrica);
    }

    @Transactional
    public void recalcularTotalVenta(Long idVenta) {

        Double total = detalle_VentRepos.sumTotalByVenta(idVenta);
        if (total == null) total = 0.0;

        Venta venta = ventaRepos.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        venta.setTotal(total);
        ventaRepos.save(venta);
    }

    private Detalle_VentDTO convertToDTO(Detalle_Vent detalle) {

        Detalle_VentDTO dto = new Detalle_VentDTO();

        dto.setIdDV(detalle.getIdDV());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubTotal(detalle.getSubTotal());

        // === TALLA DTO ===
        Talla talla = detalle.getTalla();
        TallaDTO tallaDTO = new TallaDTO();
        tallaDTO.setIdTalla(talla.getIdTalla());
        tallaDTO.setSize(talla.getSize());
        tallaDTO.setCantidad(talla.getCantidad());
        dto.setTalla(tallaDTO);

        // === PRENDA DTO ===
        Prenda prenda = talla.getPrenda();
        PrendaDTO prendaDTO = new PrendaDTO();
        prendaDTO.setIdPrenda(prenda.getIdPrenda());
        prendaDTO.setDescripcion(prenda.getDescripcion());
        prendaDTO.setStock(prenda.getStock());
        prendaDTO.setColor(prenda.getColor());
        prendaDTO.setEstado(prenda.getEstado());
        dto.setPrenda(prendaDTO);

        // === VENTA DTO ===
        Venta venta = detalle.getVenta();
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setIdVenta(venta.getIdVenta());
        ventaDTO.setFechahoraVenta(venta.getFechahoraVenta());
        ventaDTO.setTotal(venta.getTotal());
        dto.setVenta(ventaDTO);

        return dto;
    }


    @Override
    @Transactional
    public Detalle_VentDTO actualizarDetalle(Long id, Detalle_VentDTO dto) {

        Detalle_Vent detalle = detalle_VentRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        Talla tallaActual = detalle.getTalla();              // talla antes de editar
        Prenda prenda = tallaActual.getPrenda();

        Talla tallaNueva = tallaRepos.findById(dto.getTalla().getIdTalla())
                .orElseThrow(() -> new RuntimeException("Talla nueva no encontrada"));

        // === 1. Si CAMBIA DE TALLA ===
        if (!tallaActual.getIdTalla().equals(tallaNueva.getIdTalla())) {

            // devolver stock a la talla anterior
            tallaActual.setCantidad(tallaActual.getCantidad() + detalle.getCantidad());
            tallaRepos.save(tallaActual);

            // validar stock de la nueva talla
            if (tallaNueva.getCantidad() < dto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente en la talla nueva");
            }

            // restar stock de la talla nueva
            tallaNueva.setCantidad(tallaNueva.getCantidad() - dto.getCantidad());
            tallaRepos.save(tallaNueva);

            // actualizar referencia
            detalle.setTalla(tallaNueva);
        }

        // === 2. Si NO cambia de talla → solo ajustar diferencia ===
        else {
            int diferencia = dto.getCantidad() - detalle.getCantidad();

            if (diferencia > 0) {
                if (tallaActual.getCantidad() < diferencia) {
                    throw new RuntimeException("Stock insuficiente para aumentar la cantidad");
                }
                tallaActual.setCantidad(tallaActual.getCantidad() - diferencia);
            } else if (diferencia < 0) {
                tallaActual.setCantidad(tallaActual.getCantidad() + Math.abs(diferencia));
            }

            tallaRepos.save(tallaActual);
        }

        // === 3. Guardar cambios del detalle ===
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setSubTotal(dto.getSubTotal());
        detalle = detalle_VentRepos.save(detalle);

        // === 4. Recalcular stock total de la prenda ===
        int nuevoStock = prenda.getTallas().stream()
                .mapToInt(Talla::getCantidad)
                .sum();
        prenda.setStock(nuevoStock);
        prendaRepos.save(prenda);

        prendaService.actualizarEstadoPrenda(prenda);

        // === 5. Recalcular total de la venta ===
        recalcularTotalVenta(detalle.getVenta().getIdVenta());

        return convertToDTO(detalle); // como ya tienes
    }


    @Override
    @Transactional
    public boolean eliminarDetalle(Long id) {
        Detalle_Vent detalle = detalle_VentRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        // Devolver stock
        Talla talla = detalle.getTalla();
        talla.setCantidad(talla.getCantidad() + detalle.getCantidad());
        tallaRepos.save(talla);

        // Actualizar estado prenda
        prendaService.actualizarEstadoPrenda(talla.getPrenda());

        detalle_VentRepos.delete(detalle);
        return true;
    }


    @Override
    public List<Detalle_VentDTO> listarDetalles() {
        return dvRepos.findAll()
                .stream()
                .map(d -> modelMapper.map(d, Detalle_VentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Detalle_VentDTO> listarPorPrenda(Long idPrenda) {
        return dvRepos.findByPrendaId(idPrenda)
                .stream()
                .map(d -> modelMapper.map(d, Detalle_VentDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<Detalle_Vent> listarPorVenta(Long idVenta) {
        return dvRepos.findByVentaIdVenta(idVenta);
    }

    @Override
    public Integer contarDetallesPorVenta(Long idVenta) {
        return dvRepos.countByVentaIdVenta(idVenta);
    }

    @Override
    public Integer totalUnidadesVendidas(Long idPrenda) {
        return Optional.ofNullable(dvRepos.totalUnidadesVendidas(idPrenda)).orElse(0);
    }

    @Override
    public Double ingresosTotales(Long idPrenda) {
        return Optional.ofNullable(dvRepos.ingresosTotales(idPrenda)).orElse(0.0);
    }

    @Override
    public Integer cantidadVentas(Long idPrenda) {
        return Optional.ofNullable(dvRepos.cantidadVentas(idPrenda)).orElse(0);
    }

    @Override
    public LocalDateTime ultimaVenta(Long idPrenda) {
        return dvRepos.ultimaVenta(idPrenda);
    }

    @Override
    public List<Object[]> rankingPrendas() {
        return dvRepos.rankingPrendas();
    }

    // Tiempo promedio entre la primera y última venta
    @Override
    public Long tiempoPromedioVenta(Long idPrenda) {
        List<LocalDateTime> fechas = dvRepos.fechasDeVenta(idPrenda);
        if (fechas == null || fechas.size() < 2) return 0L;

        LocalDateTime primera = fechas.get(0);
        LocalDateTime ultima = fechas.get(fechas.size() - 1);

        return ChronoUnit.DAYS.between(primera, ultima);
    }
}