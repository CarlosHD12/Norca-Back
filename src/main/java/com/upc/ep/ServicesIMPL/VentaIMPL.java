package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.*;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VentaIMPL implements VentaService {
    @Autowired
    private VentaRepos ventaRepos;

    @Autowired
    private InventarioRepos inventarioRepos;

    @Autowired
    private Detalle_VentRepos detalleVentRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private LoteRepos loteRepos;

    @Autowired
    private MetricaRepos metricaRepos;

    @Override
    @Transactional
    public VentaDTO registrarVenta(VentaDTO ventaDTO) {
        Venta venta = crearVenta(ventaDTO);
        double totalVenta = 0.0;
        List<Detalle_VentDTO> detalleDTOs = new ArrayList<>();
        Set<Lote> lotesAfectados = new HashSet<>();
        Set<Prenda> prendasAfectadas = new HashSet<>();
        for (Detalle_VentDTO dvDTO : ventaDTO.getDetalles()) {
            Inventario inv = inventarioRepos.findById(dvDTO.getInventario().getIdInventario())
                    .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
            validarStock(inv, dvDTO.getCantidad());
            Detalle_Vent detalle = crearDetalleVenta(venta, inv, dvDTO.getCantidad());
            detalleVentRepos.save(detalle);
            actualizarStockInventario(inv, dvDTO.getCantidad());
            totalVenta += detalle.getSubtotal();
            detalleDTOs.add(convertirDetalleDTO(detalle));
            lotesAfectados.add(inv.getLote());
            prendasAfectadas.add(inv.getLote().getPrenda());
        }
        int totalUnidades = ventaDTO.getDetalles().stream()
                .mapToInt(Detalle_VentDTO::getCantidad)
                .sum();
        venta.setUnidades(totalUnidades);
        venta.setTotal(totalVenta);
        ventaRepos.save(venta);
        actualizarLotes(lotesAfectados);
        actualizarPrendas(prendasAfectadas);
        actualizarMetricas(prendasAfectadas, venta);
        actualizarRanking();
        return construirRespuestaVenta(venta, detalleDTOs);
    }

    private Venta crearVenta(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        venta.setCodigo("V-" + System.currentTimeMillis());
        venta.setFechaHora(LocalDateTime.now());
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setTotal(0.0);
        venta.setEstado(true);
        venta.setUnidades(0);
        return ventaRepos.save(venta);
    }

    private void validarStock(Inventario inv, int cantidad) {
        if (inv.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para talla " + inv.getTalla().getNombre());
        }
    }

    private Detalle_Vent crearDetalleVenta(Venta venta, Inventario inv, int cantidad) {
        Lote lote = inv.getLote();
        double precioVenta = lote.getPrecioVenta();
        double costoUnitario = lote.getPrecioCompraTotal() / lote.getCantidad();
        int stockAntes = inv.getStock();
        int stockDespues = stockAntes - cantidad;
        Detalle_Vent detalle = new Detalle_Vent();
        detalle.setVenta(venta);
        detalle.setInventario(inv);
        detalle.setCantidad(cantidad);
        detalle.setPrecioVentaUnitario(precioVenta);
        detalle.setCostoUnitario(costoUnitario);
        detalle.setSubtotal(cantidad * precioVenta);
        detalle.setStockAntes(stockAntes);
        detalle.setStockDespues(stockDespues);
        return detalle;
    }

    private void actualizarStockInventario(Inventario inv, int cantidad) {
        inv.setStock(inv.getStock() - cantidad);
        inventarioRepos.save(inv);
    }

    private void actualizarLotes(Set<Lote> lotes) {
        for (Lote lote : lotes) {
            int stockTotal = inventarioRepos.sumStockByLote(lote.getIdLote());
            lote.setStockActual(stockTotal);
            lote.setActivo(stockTotal > 0);
            loteRepos.save(lote);
        }
    }

    private void actualizarPrendas(Set<Prenda> prendas) {
        for (Prenda prenda : prendas) {
            int stockTotal = inventarioRepos.sumStockByPrenda(prenda.getIdPrenda());
            if (stockTotal > 0) {
                prenda.setEstado("DISPONIBLE");
            } else {
                prenda.setEstado("AGOTADO");
            }
            prendaRepos.save(prenda);
        }
    }

    private void actualizarMetricas(Set<Prenda> prendas, Venta venta) {
        for (Prenda prenda : prendas) {
            Metrica metrics = metricaRepos.findByPrenda(prenda)
                    .orElseGet(() -> {
                        Metrica m = new Metrica();
                        m.setPrenda(prenda);
                        m.setUnidadesVendidas(0);
                        m.setIngresosTotales(0.0);
                        m.setVentasRealizadas(0);
                        return m;
                    });

            Object[] result = (Object[]) metricaRepos
                    .sumVentasByPrendaYVenta(prenda.getIdPrenda(), venta.getIdVenta());

            int unidades = 0;
            double ingresos = 0.0;
            if (result != null) {
                unidades = result[0] == null ? 0 : ((Number) result[0]).intValue();
                ingresos = result[1] == null ? 0 : ((Number) result[1]).doubleValue();
            }

            Double inversion = metricaRepos.sumPrecioCompraTotalByPrenda(prenda.getIdPrenda());
            inversion = inversion == null ? 0 : inversion;

            metrics.setUnidadesVendidas(metrics.getUnidadesVendidas() + unidades);
            metrics.setIngresosTotales(metrics.getIngresosTotales() + ingresos);
            metrics.setInversionTotal(inversion);

            double ganancia = metrics.getIngresosTotales() - inversion;
            metrics.setGananciaAcumulada(ganancia);
            metrics.setVentasRealizadas(metrics.getVentasRealizadas() + 1);

            metrics.setUltimaVenta(venta.getFechaHora());

            List<LocalDateTime> fechas = metricaRepos.findAllFechasByPrenda(prenda.getIdPrenda());
            metrics.setTiempoPromedioEntreVentas(calcularTiempoPromedio(fechas));

            metrics.setRoi(inversion > 0 ? ganancia / inversion : 0);

            metricaRepos.save(metrics);
        }
    }

    private VentaDTO construirRespuestaVenta(Venta venta, List<Detalle_VentDTO> detalles) {
        VentaDTO resp = new VentaDTO();
        resp.setIdVenta(venta.getIdVenta());
        resp.setCodigo(venta.getCodigo());
        resp.setFechaHora(venta.getFechaHora());
        resp.setMetodoPago(venta.getMetodoPago());
        resp.setTotal(venta.getTotal());
        resp.setEstado(venta.getEstado());
        resp.setDetalles(detalles);
        return resp;
    }

    private double calcularTiempoPromedio(List<LocalDateTime> fechas) {
        if (fechas.size() < 2) return 0.0;
        long totalHoras = 0;
        for (int i = 1; i < fechas.size(); i++) {
            totalHoras += Duration.between(fechas.get(i - 1), fechas.get(i)).toHours();
        }
        return totalHoras / (double)(fechas.size() - 1);
    }

    private Detalle_VentDTO convertirDetalleDTO(Detalle_Vent detalle) {
        Detalle_VentDTO dto = new Detalle_VentDTO();
        dto.setIdDV(detalle.getIdDV());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubtotal(detalle.getSubtotal());
        dto.setPrecioVentaUnitario(detalle.getCostoUnitario());

        VentaDTO ventaMini = new VentaDTO();
        ventaMini.setIdVenta(detalle.getVenta().getIdVenta());
        dto.setVenta(ventaMini);

        InventarioDTO inventarioMini = new InventarioDTO();
        inventarioMini.setIdInventario(detalle.getInventario().getIdInventario());
        dto.setInventario(inventarioMini);

        return dto;
    }

    @Transactional
    public void actualizarRanking() {
        List<Metrica> todasMetricas = metricaRepos.findAll();
        todasMetricas.sort((m1, m2) -> m2.getUnidadesVendidas() - m1.getUnidadesVendidas());
        int rank = 1;
        for (Metrica m : todasMetricas) {
            m.setRanking(rank++);
            metricaRepos.save(m);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> listarVentas() {
        return ventaRepos.findAllByOrderByIdVentaDesc().stream().map(venta -> {
            VentaDTO dto = new VentaDTO();
            dto.setIdVenta(venta.getIdVenta());
            dto.setCodigo(venta.getCodigo());
            dto.setFechaHora(venta.getFechaHora());
            dto.setMetodoPago(venta.getMetodoPago());
            dto.setTotal(venta.getTotal());
            dto.setEstado(venta.getEstado());
            List<Detalle_VentDTO> detallesDTO = venta.getDetalle_Vents().stream().map(dv -> {
                Detalle_VentDTO dvDTO = new Detalle_VentDTO();
                dvDTO.setIdDV(dv.getIdDV());
                dvDTO.setCantidad(dv.getCantidad());
                dvDTO.setPrecioVentaUnitario(dv.getPrecioVentaUnitario());
                dvDTO.setCostoUnitario(dv.getCostoUnitario());
                dvDTO.setSubtotal(dv.getSubtotal());
                dvDTO.setStockAntes(dv.getStockAntes());
                dvDTO.setStockDespues(dv.getStockDespues());
                VentaDTO ventaMini = new VentaDTO();
                ventaMini.setIdVenta(venta.getIdVenta());
                dvDTO.setVenta(ventaMini);
                InventarioDTO inventarioMini = new InventarioDTO();
                inventarioMini.setIdInventario(dv.getInventario().getIdInventario());
                dvDTO.setInventario(inventarioMini);
                return dvDTO;

            }).collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO) {
        Venta venta = ventaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setEstado(ventaDTO.getEstado());
        venta = ventaRepos.save(venta);
        VentaDTO resp = new VentaDTO();
        resp.setIdVenta(venta.getIdVenta());
        resp.setCodigo(venta.getCodigo());
        resp.setFechaHora(venta.getFechaHora());
        resp.setMetodoPago(venta.getMetodoPago());
        resp.setTotal(venta.getTotal());
        resp.setEstado(venta.getEstado());
        return resp;
    }

    @Override
    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado(false);
        ventaRepos.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDetalleDTO obtenerDetalleVenta(Long idVenta) {
        Venta venta = ventaRepos.obtenerDetalleVenta(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + idVenta));
        VentaDetalleDTO ventaDTO = new VentaDetalleDTO();
        ventaDTO.setIdVenta(venta.getIdVenta());
        ventaDTO.setCodigo(venta.getCodigo());
        ventaDTO.setFechaHora(venta.getFechaHora());
        ventaDTO.setMetodoPago(venta.getMetodoPago());
        ventaDTO.setTotal(venta.getTotal());
        ventaDTO.setEstado(venta.getEstado());
        Map<Long, PrendaDetalleVentaDTO> prendaMap = new HashMap<>();
        for (Detalle_Vent detalle : venta.getDetalle_Vents()) {
            var prenda = detalle.getInventario().getLote().getPrenda();
            Long idPrenda = prenda.getIdPrenda();
            PrendaDetalleVentaDTO prendaDTO = prendaMap.get(idPrenda);
            if (prendaDTO == null) {
                prendaDTO = new PrendaDetalleVentaDTO();
                prendaDTO.setCategoria(prenda.getCategoria().getNombre());
                prendaDTO.setMarca(prenda.getMarca().getNombre());
                prendaDTO.setMaterial(prenda.getMaterial());
                prendaDTO.setDescripcion(prenda.getDescripcion());
                prendaDTO.setPrecioVentaUnitario(detalle.getPrecioVentaUnitario());
                prendaDTO.setTotalPrenda(0.0);
                byte[] imagenBytes = prenda.getCategoria().getImagenBytes();
                if (imagenBytes != null) {
                    String base64 = Base64.getEncoder().encodeToString(imagenBytes);
                    prendaDTO.setImagen("data:image/png;base64," + base64);
                } else {
                    prendaDTO.setImagen("assets/default.png");
                }
                prendaDTO.setDetallesTalla(new ArrayList<>());
                prendaMap.put(idPrenda, prendaDTO);
            }
            prendaDTO.setTotalPrenda(
                    prendaDTO.getTotalPrenda() + detalle.getSubtotal()
            );
            TallaDetalleDTO tallaDTO = new TallaDetalleDTO();
            tallaDTO.setTalla(detalle.getInventario().getTalla().getNombre());
            tallaDTO.setCantidad(detalle.getCantidad());
            tallaDTO.setSubtotal(detalle.getSubtotal());
            prendaDTO.getDetallesTalla().add(tallaDTO);
        }
        ventaDTO.setPrendas(new ArrayList<>(prendaMap.values()));
        return ventaDTO;
    }

    @Override
    public void desactivarVenta(Long idVenta) {
        Venta venta = ventaRepos.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + idVenta));
        venta.setEstado(false);
        ventaRepos.save(venta);
    }

    @Override
    public VentasTotalesDTO obtenerKpiVentas() {
        Long total = ventaRepos.totalVentas();
        LocalDateTime inicioMesActual = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime inicioMesAnterior = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime finMesAnterior = inicioMesActual;
        Long ventasUltimoMes = ventaRepos.ventasUltimoMes(inicioMesActual);
        Long ventasMesAnterior = ventaRepos.ventasUltimoMes(inicioMesAnterior);
        Double crecimiento = 0.0;
        if (ventasMesAnterior > 0) {
            crecimiento = ((ventasUltimoMes - ventasMesAnterior) * 100.0) / ventasMesAnterior;
        }
        return new VentasTotalesDTO(total, ventasUltimoMes, crecimiento);
    }

    @Override
    public VentasTotalesDTO obtenerKpiUnidades() {
        Long total = ventaRepos.unidadesTotales();
        LocalDateTime inicioMesActual = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime inicioMesAnterior = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();
        Long unidadesUltimoMes = ventaRepos.unidadesUltimoMes(inicioMesActual);
        Long unidadesMesAnterior = ventaRepos.unidadesUltimoMes(inicioMesAnterior);
        Double crecimiento = 0.0;
        if (unidadesMesAnterior > 0) {
            crecimiento = ((unidadesUltimoMes - unidadesMesAnterior) * 100.0) / unidadesMesAnterior;
        }
        return new VentasTotalesDTO(total, unidadesUltimoMes, crecimiento);
    }

    @Override
    public VentasTotalesDTO obtenerIngresosTotales() {
        Long ingresosTotales = ventaRepos.ingresosTotales().longValue();
        LocalDateTime inicioMesActual = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime inicioMesAnterior = inicioMesActual.minusMonths(1);
        Long ingresosUltimoMes = ventaRepos
                .ingresosUltimoMes(inicioMesActual)
                .longValue();
        Long ingresosMesAnterior = ventaRepos
                .ingresosMesAnterior(inicioMesAnterior, inicioMesActual)
                .longValue();
        double crecimiento = 0;
        if (ingresosMesAnterior > 0) {
            crecimiento =
                    ((double)(ingresosUltimoMes - ingresosMesAnterior)
                            / ingresosMesAnterior) * 100;
        }
        return new VentasTotalesDTO(
                ingresosTotales,
                ingresosUltimoMes,
                crecimiento
        );
    }

    @Override
    public MetodoPagoDTO obtenerMetodoPagoFavorito() {
        Pageable topUno = PageRequest.of(0,1);
        List<MetodoPagoDTO> lista = ventaRepos.metodoPagoMasUsado(topUno);
        if(lista.isEmpty()){
            return new MetodoPagoDTO("SIN VENTAS",0L);
        }
        return lista.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresosCategoriaDTO> obtenerIngresosPorCategoria() {
        return ventaRepos.obtenerIngresosPorCategoria();
    }


    @Override
    public List<Map<String, Object>> reportePorMes(String tipo) {
        List<VentasMesDTO> lista = ventaRepos.obtenerVentasOMes();
        return lista.stream().map(vm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("mes", vm.getMes());
            map.put("anio", vm.getAnio());
            if ("ventas".equalsIgnoreCase(tipo)) {
                map.put("valor", vm.getTotalVentas());
            } else if ("ingresos".equalsIgnoreCase(tipo)) {
                map.put("valor", vm.getGanancias());
            } else {
                map.put("valor", 0);
            }
            return map;
        }).toList();
    }

    @Override
    public Page<VentaListadoDTO> listarVentas(
            String codigo,
            String metodoPago,
            String periodo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Double precioMin,
            Double precioMax,
            Integer unidadesMin,
            Integer unidadesMax,
            Pageable pageable) {

        return ventaRepos.listarVentas(
                codigo,
                metodoPago,
                periodo,
                fechaInicio,
                fechaFin,
                precioMin,
                precioMax,
                unidadesMin,
                unidadesMax,
                pageable
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ImpactoVentaDTO obtenerImpactoVenta(Long idVenta) {
        Venta venta = ventaRepos.findVentaWithAll(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        ImpactoVentaDTO dto = new ImpactoVentaDTO();
        dto.setCodigoVenta(venta.getCodigo());
        dto.setFecha(venta.getFechaHora());
        dto.setIngresoTotal(venta.getTotal());
        double costoTotal = 0.0;
        List<ImpactoProductoDTO> productos = new ArrayList<>();
        for (Detalle_Vent dv : venta.getDetalle_Vents()) {
            Inventario inv = dv.getInventario();
            Prenda prenda = inv.getLote().getPrenda();
            double costoUnitario = dv.getCostoUnitario();
            double costo = costoUnitario * dv.getCantidad();
            double ingreso = dv.getSubtotal();
            double ganancia = ingreso - costo;
            costoTotal += costo;
            ImpactoProductoDTO prod = new ImpactoProductoDTO();
            prod.setCategoria(prenda.getCategoria().getNombre());
            prod.setMarca(prenda.getMarca().getNombre());
            prod.setCalidad(prenda.getEstado());
            byte[] imagen = prenda.getCategoria().getImagenBytes();
            if (imagen != null) {
                String base64 = Base64.getEncoder().encodeToString(imagen);
                prod.setImagenCategoria(base64);
            }
            prod.setCantidadVendida(dv.getCantidad());
            prod.setIngreso(ingreso);
            prod.setCosto(costo);
            prod.setGanancia(ganancia);
            ImpactoInventarioDTO invDTO = new ImpactoInventarioDTO();
            invDTO.setTalla(inv.getTalla().getNombre());
            invDTO.setStockAntes(dv.getStockAntes());
            invDTO.setStockDespues(dv.getStockDespues());
            prod.setInventarios(List.of(invDTO));
            productos.add(prod);
        }
        dto.setCostoTotal(costoTotal);
        dto.setGanancia(venta.getTotal() - costoTotal);
        dto.setMargen((dto.getGanancia() / venta.getTotal()) * 100);
        dto.setProductos(productos);
        if (dto.getMargen() > 40) {
            dto.setAnalisis("Venta altamente rentable");
        } else if (dto.getMargen() > 20) {
            dto.setAnalisis("Venta con rentabilidad media");
        } else {
            dto.setAnalisis("Venta con baja rentabilidad");
        }
        return dto;
    }
}