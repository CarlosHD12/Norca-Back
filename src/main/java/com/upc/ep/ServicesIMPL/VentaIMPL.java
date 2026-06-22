package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.*;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.LoteService;
import com.upc.ep.Services.MovimientoService;
import com.upc.ep.Services.PrendaService;
import com.upc.ep.Services.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VentaIMPL implements VentaService {
    @Autowired
    private VentaRepos ventaRepos;

    @Autowired
    private InventarioRepos inventarioRepos;

    @Autowired
    private MetricaRepos metricaRepos;

    @Autowired
    private DetalleVentRepos detalleVentaRepos;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private PrendaService prendaService;

    @Autowired
    private LoteService loteService;

    @Override
    @Transactional
    public VentaResponseDTO registrarVenta(VentaRegistroDTO dto) {
        Venta venta = new Venta();
        venta.setCodigo(generarCodigoVenta());
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setEstadoVenta(Venta.EstadoVenta.COMPLETADA);
        venta.setActivo(true);
        venta.setTotal(BigDecimal.ZERO);
        venta.setUnidades(0);
        Venta ventaGuardada = ventaRepos.save(venta);
        BigDecimal totalVenta = BigDecimal.ZERO;
        Integer totalUnidades = 0;
        List<DetalleVenta> detallesVenta = new ArrayList<>();
        for (DetalleVentaRegistroDTO detalleDTO : dto.getDetalles()) {
            Inventario inventario = inventarioRepos.findById(detalleDTO.getInventarioId()).orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
            Lote lote = inventario.getLote();
            Prenda prenda = lote.getPrenda();
            if (!prenda.getActivo() || prenda.getEstado() == Prenda.EstadoPrenda.INHABILITADA) {
                throw new RuntimeException("La prenda no está disponible");
            }
            if (!Boolean.TRUE.equals(lote.getActivo()) || lote.getStockActual() <= 0) {
                throw new RuntimeException("El lote ya no se encuentra disponible");
            }
            Lote loteFIFO = loteService.obtenerLoteFIFOActivo(prenda.getIdPrenda());
            if (!lote.getIdLote().equals(loteFIFO.getIdLote())) {
                throw new RuntimeException("Solo se puede vender desde el lote FIFO activo");
            }
            if (detalleDTO.getCantidad() > inventario.getStock()) {
                throw new RuntimeException("Stock insuficiente para talla " + inventario.getTalla().getNombre());
            }
            Integer stockAntes = inventario.getStock();
            Integer stockDespues = stockAntes - detalleDTO.getCantidad();
            inventario.setStock(stockDespues);
            inventarioRepos.save(inventario);
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(ventaGuardada);
            detalleVenta.setInventario(inventario);
            detalleVenta.setCantidad(detalleDTO.getCantidad());
            detalleVenta.setPrecioVentaUnitario(lote.getPrecioVenta());
            BigDecimal costoUnitario = lote.getPrecioCompraTotal().divide(BigDecimal.valueOf(lote.getCantidadInicial()), 2, RoundingMode.HALF_UP);
            detalleVenta.setCostoUnitario(costoUnitario);
            detalleVenta.setStockAntes(stockAntes);
            detalleVenta.setStockDespues(stockDespues);
            detalleVenta.calcularSubtotal();
            detalleVentaRepos.save(detalleVenta);
            detallesVenta.add(detalleVenta);
            totalVenta = totalVenta.add(detalleVenta.getSubtotal());
            totalUnidades += detalleDTO.getCantidad();
            loteService.actualizarStockLote(lote.getIdLote());
            prendaService.validarPrendaAgotada(prenda.getIdPrenda());
            Metrica metrica = prenda.getMetrica();
            BigDecimal ganancia = lote.getPrecioVenta().subtract(costoUnitario).multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
            metrica.setUnidadesVendidas(metrica.getUnidadesVendidas() + detalleDTO.getCantidad());
            metrica.setIngresosTotales(metrica.getIngresosTotales().add(detalleVenta.getSubtotal()));
            metrica.setGananciaAcumulada(metrica.getGananciaAcumulada().add(ganancia));
            metrica.setVentasRealizadas(metrica.getVentasRealizadas() + 1);
            metrica.setUltimaVenta(LocalDateTime.now());
            metricaRepos.save(metrica);
        }
        ventaGuardada.setTotal(totalVenta);
        ventaGuardada.setUnidades(totalUnidades);
        ventaGuardada.getDetalleVentas().addAll(detallesVenta);
        ventaRepos.save(ventaGuardada);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.VENTA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_VENTA)
                        .entidadId(venta.getIdVenta())
                        .codigoReferencia(venta.getCodigo())
                        .motivo("Se registró la venta " + venta.getCodigo())
                        .build()
        );
        return VentaResponseDTO.builder()
                .idVenta(ventaGuardada.getIdVenta())
                .codigo(ventaGuardada.getCodigo())
                .unidades(ventaGuardada.getUnidades())
                .total(ventaGuardada.getTotal())
                .metodoPago(ventaGuardada.getMetodoPago().name())
                .estadoVenta(ventaGuardada.getEstadoVenta().name())
                .fechaHora(ventaGuardada.getFechaHora())
                .build();
    }

    private String generarCodigoVenta() {
        return "VNT-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    @Override
    @Transactional
    public void anularVenta(Long idVenta) {
        Venta venta = ventaRepos.findById(idVenta).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        if (venta.getEstadoVenta() == Venta.EstadoVenta.ANULADA) {
            throw new RuntimeException("La venta ya fue anulada");
        }
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Inventario inventario = detalle.getInventario();
            Lote lote = inventario.getLote();
            Prenda prenda = lote.getPrenda();
            Integer stockAntes = inventario.getStock();
            Integer stockDespues = stockAntes + detalle.getCantidad();
            inventario.setStock(stockDespues);
            inventarioRepos.save(inventario);
            loteService.actualizarStockLote(lote.getIdLote());
            prendaService.validarPrendaAgotada(prenda.getIdPrenda());
            Metrica metrica = prenda.getMetrica();
            BigDecimal ganancia = detalle.getPrecioVentaUnitario().subtract(detalle.getCostoUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad()));
            metrica.setUnidadesVendidas(metrica.getUnidadesVendidas() - detalle.getCantidad());
            metrica.setIngresosTotales(metrica.getIngresosTotales().subtract(detalle.getSubtotal()));
            metrica.setGananciaAcumulada(metrica.getGananciaAcumulada().subtract(ganancia));
            if (metrica.getVentasRealizadas() > 0) {metrica.setVentasRealizadas(metrica.getVentasRealizadas() - 1);}
            metricaRepos.save(metrica);
        }
        venta.setEstadoVenta(Venta.EstadoVenta.ANULADA);
        ventaRepos.save(venta);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.VENTA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.ANULACION_VENTA)
                        .entidadId(venta.getIdVenta())
                        .codigoReferencia(venta.getCodigo())
                        .motivo("Se anuló la venta " + venta.getCodigo())
                        .build()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaListResponseDTO> listarVentas(Pageable pageable) {
        Page<Venta> ventas = ventaRepos.findByActivoTrue(pageable);
        return ventas.map(this::toListDTO);
    }

    private VentaListResponseDTO toListDTO(Venta venta) {
        return VentaListResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .codigo(venta.getCodigo())
                .unidades(venta.getUnidades())
                .total(venta.getTotal())
                .metodoPago(venta.getMetodoPago().name())
                .estadoVenta(venta.getEstadoVenta().name())
                .fechaHora(venta.getFechaHora())
                .build();
    }

//
//    @Override
//    @Transactional(readOnly = true)
//    public VentaDetalleDTO obtenerDetalleVenta(Long idVenta) {
//        Venta venta = ventaRepos.obtenerDetalleVenta(idVenta)
//                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + idVenta));
//        VentaDetalleDTO ventaDTO = new VentaDetalleDTO();
//        ventaDTO.setIdVenta(venta.getIdVenta());
//        ventaDTO.setCodigo(venta.getCodigo());
//        ventaDTO.setFechaHora(venta.getFechaHora());
//        ventaDTO.setMetodoPago(venta.getMetodoPago());
//        ventaDTO.setTotal(venta.getTotal());
//        ventaDTO.setEstado(venta.getEstado());
//        Map<Long, PrendaDetalleVentaDTO> prendaMap = new HashMap<>();
//        for (DetalleVenta detalle : venta.getDetalle_Vents()) {
//            var prenda = detalle.getInventario().getLote().getPrenda();
//            Long idPrenda = prenda.getIdPrenda();
//            PrendaDetalleVentaDTO prendaDTO = prendaMap.get(idPrenda);
//            if (prendaDTO == null) {
//                prendaDTO = new PrendaDetalleVentaDTO();
//                prendaDTO.setCategoria(prenda.getCategoria().getNombre());
//                prendaDTO.setMarca(prenda.getMarca().getNombre());
//                prendaDTO.setMaterial(prenda.getMaterial());
//                prendaDTO.setDescripcion(prenda.getDescripcion());
//                prendaDTO.setPrecioVentaUnitario(detalle.getPrecioVentaUnitario());
//                prendaDTO.setTotalPrenda(0.0);
//                byte[] imagenBytes = prenda.getCategoria().getImagenBytes();
//                if (imagenBytes != null) {
//                    String base64 = Base64.getEncoder().encodeToString(imagenBytes);
//                    prendaDTO.setImagen("data:image/png;base64," + base64);
//                } else {
//                    prendaDTO.setImagen("assets/default.png");
//                }
//                prendaDTO.setDetallesTalla(new ArrayList<>());
//                prendaMap.put(idPrenda, prendaDTO);
//            }
//            prendaDTO.setTotalPrenda(
//                    prendaDTO.getTotalPrenda() + detalle.getSubtotal()
//            );
//            TallaDetalleDTO tallaDTO = new TallaDetalleDTO();
//            tallaDTO.setTalla(detalle.getInventario().getTalla().getNombre());
//            tallaDTO.setCantidad(detalle.getCantidad());
//            tallaDTO.setSubtotal(detalle.getSubtotal());
//            prendaDTO.getDetallesTalla().add(tallaDTO);
//        }
//        ventaDTO.setPrendas(new ArrayList<>(prendaMap.values()));
//        return ventaDTO;
//    }
//    @Override
//    public VentasTotalesDTO obtenerKpiVentas() {
//        Long total = ventaRepos.totalVentas();
//        LocalDateTime inicioMesActual = LocalDate.now()
//                .withDayOfMonth(1)
//                .atStartOfDay();
//        LocalDateTime inicioMesAnterior = LocalDate.now()
//                .minusMonths(1)
//                .withDayOfMonth(1)
//                .atStartOfDay();
//        LocalDateTime finMesAnterior = inicioMesActual;
//        Long ventasUltimoMes = ventaRepos.ventasUltimoMes(inicioMesActual);
//        Long ventasMesAnterior = ventaRepos.ventasUltimoMes(inicioMesAnterior);
//        Double crecimiento = 0.0;
//        if (ventasMesAnterior > 0) {
//            crecimiento = ((ventasUltimoMes - ventasMesAnterior) * 100.0) / ventasMesAnterior;
//        }
//        return new VentasTotalesDTO(total, ventasUltimoMes, crecimiento);
//    }
//
//    @Override
//    public VentasTotalesDTO obtenerKpiUnidades() {
//        Long total = ventaRepos.unidadesTotales();
//        LocalDateTime inicioMesActual = LocalDate.now()
//                .withDayOfMonth(1)
//                .atStartOfDay();
//        LocalDateTime inicioMesAnterior = LocalDate.now()
//                .minusMonths(1)
//                .withDayOfMonth(1)
//                .atStartOfDay();
//        Long unidadesUltimoMes = ventaRepos.unidadesUltimoMes(inicioMesActual);
//        Long unidadesMesAnterior = ventaRepos.unidadesUltimoMes(inicioMesAnterior);
//        Double crecimiento = 0.0;
//        if (unidadesMesAnterior > 0) {
//            crecimiento = ((unidadesUltimoMes - unidadesMesAnterior) * 100.0) / unidadesMesAnterior;
//        }
//        return new VentasTotalesDTO(total, unidadesUltimoMes, crecimiento);
//    }
//
//    @Override
//    public VentasTotalesDTO obtenerIngresosTotales() {
//        Long ingresosTotales = ventaRepos.ingresosTotales().longValue();
//        LocalDateTime inicioMesActual = LocalDate.now()
//                .withDayOfMonth(1)
//                .atStartOfDay();
//        LocalDateTime inicioMesAnterior = inicioMesActual.minusMonths(1);
//        Long ingresosUltimoMes = ventaRepos
//                .ingresosUltimoMes(inicioMesActual)
//                .longValue();
//        Long ingresosMesAnterior = ventaRepos
//                .ingresosMesAnterior(inicioMesAnterior, inicioMesActual)
//                .longValue();
//        double crecimiento = 0;
//        if (ingresosMesAnterior > 0) {
//            crecimiento =
//                    ((double)(ingresosUltimoMes - ingresosMesAnterior)
//                            / ingresosMesAnterior) * 100;
//        }
//        return new VentasTotalesDTO(
//                ingresosTotales,
//                ingresosUltimoMes,
//                crecimiento
//        );
//    }
//
//    @Override
//    public MetodoPagoDTO obtenerMetodoPagoFavorito() {
//        Pageable topUno = PageRequest.of(0,1);
//        List<MetodoPagoDTO> lista = ventaRepos.metodoPagoMasUsado(topUno);
//        if(lista.isEmpty()){
//            return new MetodoPagoDTO("SIN VENTAS",0L);
//        }
//        return lista.get(0);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<IngresosCategoriaDTO> obtenerIngresosPorCategoria() {
//        return ventaRepos.obtenerIngresosPorCategoria();
//    }
//
//
//    @Override
//    public List<Map<String, Object>> reportePorMes(String tipo) {
//        List<VentasMesDTO> lista = ventaRepos.obtenerVentasOMes();
//        return lista.stream().map(vm -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("mes", vm.getMes());
//            map.put("anio", vm.getAnio());
//            if ("ventas".equalsIgnoreCase(tipo)) {
//                map.put("valor", vm.getTotalVentas());
//            } else if ("ingresos".equalsIgnoreCase(tipo)) {
//                map.put("valor", vm.getGanancias());
//            } else {
//                map.put("valor", 0);
//            }
//            return map;
//        }).toList();
//    }
//
//    @Override
//    public Page<VentaListadoDTO> listarVentas(
//            String codigo,
//            String metodoPago,
//            String periodo,
//            LocalDateTime fechaInicio,
//            LocalDateTime fechaFin,
//            Double precioMin,
//            Double precioMax,
//            Integer unidadesMin,
//            Integer unidadesMax,
//            Pageable pageable) {
//
//        return ventaRepos.listarVentas(
//                codigo,
//                metodoPago,
//                periodo,
//                fechaInicio,
//                fechaFin,
//                precioMin,
//                precioMax,
//                unidadesMin,
//                unidadesMax,
//                pageable
//        );
//    }
//
//    @Transactional(readOnly = true)
//    @Override
//    public ImpactoVentaDTO obtenerImpactoVenta(Long idVenta) {
//        Venta venta = ventaRepos.findVentaWithAll(idVenta)
//                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
//        ImpactoVentaDTO dto = new ImpactoVentaDTO();
//        dto.setCodigoVenta(venta.getCodigo());
//        dto.setFecha(venta.getFechaHora());
//        dto.setIngresoTotal(venta.getTotal());
//        double costoTotal = 0.0;
//        List<ImpactoProductoDTO> productos = new ArrayList<>();
//        for (DetalleVenta dv : venta.getDetalle_Vents()) {
//            Inventario inv = dv.getInventario();
//            Prenda prenda = inv.getLote().getPrenda();
//            double costoUnitario = dv.getCostoUnitario();
//            double costo = costoUnitario * dv.getCantidad();
//            double ingreso = dv.getSubtotal();
//            double ganancia = ingreso - costo;
//            costoTotal += costo;
//            ImpactoProductoDTO prod = new ImpactoProductoDTO();
//            prod.setCategoria(prenda.getCategoria().getNombre());
//            prod.setMarca(prenda.getMarca().getNombre());
//            prod.setCalidad(prenda.getEstado());
//            byte[] imagen = prenda.getCategoria().getImagenBytes();
//            if (imagen != null) {
//                String base64 = Base64.getEncoder().encodeToString(imagen);
//                prod.setImagenCategoria(base64);
//            }
//            prod.setCantidadVendida(dv.getCantidad());
//            prod.setIngreso(ingreso);
//            prod.setCosto(costo);
//            prod.setGanancia(ganancia);
//            ImpactoInventarioDTO invDTO = new ImpactoInventarioDTO();
//            invDTO.setTalla(inv.getTalla().getNombre());
//            invDTO.setStockAntes(dv.getStockAntes());
//            invDTO.setStockDespues(dv.getStockDespues());
//            prod.setInventarios(List.of(invDTO));
//            productos.add(prod);
//        }
//        dto.setCostoTotal(costoTotal);
//        dto.setGanancia(venta.getTotal() - costoTotal);
//        dto.setMargen((dto.getGanancia() / venta.getTotal()) * 100);
//        dto.setProductos(productos);
//        if (dto.getMargen() > 40) {
//            dto.setAnalisis("Venta altamente rentable");
//        } else if (dto.getMargen() > 20) {
//            dto.setAnalisis("Venta con rentabilidad media");
//        } else {
//            dto.setAnalisis("Venta con baja rentabilidad");
//        }
//        return dto;
//    }
}