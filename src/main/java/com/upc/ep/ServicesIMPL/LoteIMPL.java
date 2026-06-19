package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.*;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoteIMPL implements LoteService {
    @Autowired
    private LoteRepos loteRepos;

    @Autowired
    private InventarioRepos inventarioRepos;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private MovimientoRepos movimientoRepos;

    @Override
    @Transactional
    public LoteResponseDTO registrarLote(LoteRegistroDTO dto) {
        Prenda prenda = prendaRepos.findById(dto.getPrendaId()).orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        if (!prenda.getActivo()) {
            throw new RuntimeException("No se puede registrar lotes a una prenda inactiva");
        }
        if (prenda.getEstado() == Prenda.EstadoPrenda.INHABILITADA) {
            throw new RuntimeException("La prenda está inhabilitada");
        }
        int sumaInventarios = dto.getInventarios()
                .stream()
                .mapToInt(InventarioRegistroDTO::getStock)
                .sum();
        if (sumaInventarios
                != dto.getCantidadInicial()) {
            throw new RuntimeException("La suma de inventarios debe coincidir con la cantidad inicial");
        }
        Set<Long> tallas = new HashSet<>();
        for (InventarioRegistroDTO inventarioDTO : dto.getInventarios()) {
            if (!tallas.add(inventarioDTO.getTallaId())) {
                throw new RuntimeException("No se permiten tallas repetidas en el mismo lote");
            }
        }
        Lote lote = new Lote();
        lote.setCodigoLote(generarCodigoLote());
        lote.setCantidadInicial(dto.getCantidadInicial());
        lote.setStockActual(dto.getCantidadInicial());
        lote.setPrecioCompraTotal(dto.getPrecioCompraTotal());
        lote.setPrecioVenta(dto.getPrecioVenta());
        lote.setActivo(true);
        lote.setPrenda(prenda);
        Lote loteGuardado = loteRepos.save(lote);
        for (InventarioRegistroDTO inventarioDTO : dto.getInventarios()) {
            Talla talla = tallaRepos.findByIdTallaAndActivoTrue(inventarioDTO.getTallaId()).orElseThrow(() -> new RuntimeException("Talla no encontrada o inactiva"));
            Inventario inventario = new Inventario();
            inventario.setStock(inventarioDTO.getStock());
            inventario.setLote(loteGuardado);
            inventario.setTalla(talla);
            inventarioRepos.save(inventario);
        }
        if (prenda.getEstado() == Prenda.EstadoPrenda.SIN_LOTES || prenda.getEstado() == Prenda.EstadoPrenda.AGOTADO) {
            prenda.setEstadoAnterior(prenda.getEstado());
            prenda.setEstado(Prenda.EstadoPrenda.DISPONIBLE);
            prendaRepos.save(prenda);
            Movimiento movimientoEstado = new Movimiento();
            movimientoEstado.setTipoMovimiento(Movimiento.TipoMovimiento.CAMBIO_ESTADO_PRENDA);
            movimientoEstado.setMotivo("La prenda pasó a DISPONIBLE por nuevo lote");
            movimientoEstado.setReferenciaId(prenda.getCodigo());
            movimientoRepos.save(movimientoEstado);
        }
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_LOTE);
        movimiento.setMotivo("Se registró el lote: " + loteGuardado.getCodigoLote());
        movimiento.setReferenciaId(loteGuardado.getCodigoLote());
        movimientoRepos.save(movimiento);
        return mapToResponse(loteGuardado);
    }

    private String generarCodigoLote() {
        String codigo;
        do {
            codigo = "LOT-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while (
                loteRepos.existsByCodigoLote(codigo)
        );
        return codigo;
    }

    private LoteResponseDTO mapToResponse(Lote lote) {
        return LoteResponseDTO.builder()
                .idLote(lote.getIdLote())
                .codigoLote(lote.getCodigoLote())
                .cantidadInicial(lote.getCantidadInicial())
                .stockActual(lote.getStockActual())
                .precioCompraTotal(lote.getPrecioCompraTotal())
                .precioVenta(lote.getPrecioVenta())
                .fechaIngreso(lote.getFechaIngreso())
                .activo(lote.getActivo())
                .prendaCodigo(lote.getPrenda().getCodigo())
                .build();
    }

    @Override
    public MetricaLoteDTO obtenerMetricasLote(Long idLote) {
        Lote lote = loteRepos.findById(idLote)
                .orElseThrow(() ->
                        new RuntimeException("Lote no encontrado")
                );
        return calcularMetricasLote(lote);
    }

    private MetricaLoteDTO calcularMetricasLote(Lote lote) {
        BigDecimal ventaTotal = lote.getPrecioVenta().multiply(BigDecimal.valueOf(lote.getCantidadInicial()));
        BigDecimal costoUnitario = lote.getPrecioCompraTotal().divide(BigDecimal.valueOf(lote.getCantidadInicial()), 2, RoundingMode.HALF_UP);
        BigDecimal gananciaPorUnidad = lote.getPrecioVenta().subtract(costoUnitario);
        BigDecimal gananciaTotal = gananciaPorUnidad.multiply(BigDecimal.valueOf(lote.getCantidadInicial()));
        BigDecimal margenGanancia = gananciaTotal.divide(lote.getPrecioCompraTotal(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal radioInversion = ventaTotal.divide(lote.getPrecioCompraTotal(), 2, RoundingMode.HALF_UP);
        Integer puntoEquilibrio = gananciaPorUnidad.compareTo(BigDecimal.ZERO) > 0 ? lote.getPrecioCompraTotal().divide(gananciaPorUnidad, 0, RoundingMode.UP).intValue() : 0;
        return MetricaLoteDTO.builder()
                .ventaTotal(ventaTotal)
                .gananciaPorUnidad(gananciaPorUnidad)
                .gananciaTotal(gananciaTotal)
                .margenGanancia(margenGanancia)
                .radioInversion(radioInversion)
                .puntoEquilibrio(puntoEquilibrio)
                .build();
    }

    @Override
    public LoteSeleccionadoDTO obtenerInventariosDisponibles(Long idPrenda) {
        Lote loteFIFO = obtenerLoteFIFOActivo(idPrenda);
        List<InventarioHistorialDTO> inventarios =
                loteFIFO.getInventarios().stream().filter(inv -> inv.getStock() > 0).map(inv -> InventarioHistorialDTO.builder().idInventario(inv.getIdInventario())
                        .talla(inv.getTalla().getNombre())
                        .stock(inv.getStock()).build()).toList();
        return LoteSeleccionadoDTO.builder()
                .idLote(loteFIFO.getIdLote())
                .codigoLote(loteFIFO.getCodigoLote())
                .precioVenta(loteFIFO.getPrecioVenta())
                .inventarios(inventarios)
                .build();
    }

    @Override
    @Transactional
    public void actualizarStockLote(Long idLote) {
        Lote lote = loteRepos.findById(idLote).orElseThrow(() -> new RuntimeException("Lote no encontrado"));
        Boolean estabaActivo = lote.getActivo();
        Integer stockActual =
                lote.getInventarios()
                        .stream()
                        .mapToInt(Inventario::getStock)
                        .sum();
        lote.setStockActual(stockActual);
        if (stockActual == 0) {
            lote.setActivo(false);
            if (Boolean.TRUE.equals(estabaActivo)) {
                Movimiento movimiento = new Movimiento();
                movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.LOTE_AGOTADO);
                movimiento.setMotivo("El lote quedó agotado: " + lote.getCodigoLote());
                movimiento.setReferenciaId(lote.getCodigoLote());
                movimientoRepos.save(movimiento);
                Optional<Lote> nuevoFIFO = loteRepos.findFirstByPrendaIdPrendaAndActivoTrueAndStockActualGreaterThanOrderByFechaIngresoAsc(
                        lote.getPrenda().getIdPrenda(), 0);
                if (nuevoFIFO.isPresent()) {Movimiento movimientoFIFO = new Movimiento();
                    movimientoFIFO.setTipoMovimiento(Movimiento.TipoMovimiento.CAMBIO_FIFO);
                    movimientoFIFO.setMotivo("El nuevo lote FIFO activo es: " + nuevoFIFO.get().getCodigoLote());
                    movimientoFIFO.setReferenciaId(lote.getPrenda().getCodigo());
                    movimientoRepos.save(movimientoFIFO);
                }
            }

        } else {
            lote.setActivo(true);
            if (!Boolean.TRUE.equals(estabaActivo)) {
                Movimiento movimiento = new Movimiento();
                movimiento.setTipoMovimiento(Movimiento.TipoMovimiento.REACTIVACION_LOTE);
                movimiento.setMotivo("El lote volvió a estar disponible: " + lote.getCodigoLote());
                movimiento.setReferenciaId(lote.getCodigoLote());
                movimientoRepos.save(movimiento);
                Movimiento movimientoFIFO = new Movimiento();
                movimientoFIFO.setTipoMovimiento(Movimiento.TipoMovimiento.CAMBIO_FIFO);
                movimientoFIFO.setMotivo("El nuevo lote FIFO activo es: " + lote.getCodigoLote());
                movimientoFIFO.setReferenciaId(lote.getPrenda().getCodigo());
                movimientoRepos.save(movimientoFIFO);
            }
        }
        loteRepos.save(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialPrendaResponseDTO listarHistorialLotes(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        List<LoteHistorialResponseDTO> lotes = loteRepos
                .listarHistorialLotes(idPrenda)
                .stream()
                .map(this::toHistorialDTO)
                .toList();
        return HistorialPrendaResponseDTO.builder()
                .idPrenda(prenda.getIdPrenda())
                .codigoPrenda(prenda.getCodigo())
                .lotes(lotes)
                .build();
    }

    private LoteHistorialResponseDTO toHistorialDTO(Lote lote) {
        List<InventarioHistorialDTO> inventarios = lote.getInventarios()
                .stream()
                .map(inventario -> InventarioHistorialDTO.builder()
                        .idInventario(inventario.getIdInventario())
                        .talla(inventario.getTalla().getNombre())
                        .stock(inventario.getStock())
                        .build())
                .toList();
        return LoteHistorialResponseDTO.builder()
                .idLote(lote.getIdLote())
                .codigoLote(lote.getCodigoLote())
                .cantidadInicial(lote.getCantidadInicial())
                .stockActual(lote.getStockActual())
                .precioCompraTotal(lote.getPrecioCompraTotal())
                .precioVenta(lote.getPrecioVenta())
                .activo(lote.getActivo())
                .fechaIngreso(lote.getFechaIngreso())
                .inventarios(inventarios)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Lote obtenerLoteFIFOActivo(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() ->
                        new RuntimeException("Prenda no encontrada"));
        if (!prenda.getActivo()) {
            throw new RuntimeException("La prenda está inactiva");
        }
        if (prenda.getEstado() != Prenda.EstadoPrenda.DISPONIBLE) {
            throw new RuntimeException("La prenda no tiene stock disponible");
        }
        return loteRepos
                .findFirstByPrendaIdPrendaAndActivoTrueAndStockActualGreaterThanOrderByFechaIngresoAsc(idPrenda, 0)
                .orElseThrow(() -> new RuntimeException("No existe lote FIFO disponible"));
    }

    @Override
    @Transactional(readOnly = true)
    public UltimoLoteResponseDTO obtenerUltimoLotePrenda(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        Lote lote = loteRepos
                .obtenerUltimoLotePrenda(prenda.getIdPrenda())
                .orElseThrow(() -> new RuntimeException("La prenda no tiene lotes registrados"));
        List<InventarioHistorialDTO> inventarios =
                lote.getInventarios()
                        .stream()
                        .map(inv -> InventarioHistorialDTO
                                .builder()
                                .idInventario(inv.getIdInventario())
                                .talla(inv.getTalla().getNombre())
                                .stock(inv.getStock())
                                .build()).toList();
        return UltimoLoteResponseDTO
                .builder()
                .idLote(lote.getIdLote())
                .codigoLote(lote.getCodigoLote())
                .cantidadInicial(lote.getCantidadInicial())
                .stockActual(lote.getStockActual())
                .precioCompraTotal(lote.getPrecioCompraTotal())
                .precioVenta(lote.getPrecioVenta())
                .activo(lote.getActivo())
                .fechaIngreso(lote.getFechaIngreso())
                .inventarios(inventarios)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenLoteDTO obtenerResumenLote(Long idLote) {
        Lote lote = loteRepos.findById(idLote).orElseThrow(() ->
                new RuntimeException("Lote no encontrado"));

        BigDecimal costoUnitario = lote.getPrecioCompraTotal()
                .divide(BigDecimal.valueOf(lote.getCantidadInicial()), 2, RoundingMode.HALF_UP);
        int vendidos = lote.getCantidadInicial() - lote.getStockActual();
        double porcentajeVendido = lote.getCantidadInicial() > 0 ? (double) vendidos * 100 / lote.getCantidadInicial() : 0;
        String estadoComercial;
        if (porcentajeVendido >= 75) {
            estadoComercial = "Excelente";
        } else if (porcentajeVendido >= 50) {
            estadoComercial = "Bueno";
        } else if (porcentajeVendido >= 25) {
            estadoComercial = "Regular";
        } else {
            estadoComercial = "Lento";
        }
        BigDecimal valorInventario = costoUnitario.multiply(BigDecimal.valueOf(lote.getStockActual()));
        BigDecimal gananciaPotencial =
                lote.getPrecioVenta().subtract(costoUnitario).multiply(
                        BigDecimal.valueOf(lote.getStockActual()));
        int antiguedadDias = (int) ChronoUnit.DAYS.between(
                lote.getFechaIngreso().toLocalDate(),
                LocalDate.now());
        return ResumenLoteDTO.builder()
                .antiguedadDias(antiguedadDias)
                .estadoComercial(estadoComercial)
                .valorInventario(valorInventario)
                .gananciaPotencial(gananciaPotencial)
                .build();
    }

//
//    @Override
//    public LotesTotalesDTO obtenerStockDisponible() {
//        Long totalStock = loteRepos.totalStockDisponible().longValue();
//        LocalDate inicioMesActual = LocalDate.now().withDayOfMonth(1);
//        LocalDate inicioMesAnterior = inicioMesActual.minusMonths(1);
//        Long stockUltimoMes = loteRepos
//                .stockUltimoMes(inicioMesActual)
//                .longValue();
//        Long stockMesAnterior = loteRepos
//                .stockMesAnterior(inicioMesAnterior, inicioMesActual)
//                .longValue();
//        double crecimiento = 0;
//        if (stockMesAnterior > 0) {
//            crecimiento = ((double)(stockUltimoMes - stockMesAnterior) / stockMesAnterior) * 100;
//        }
//        return new LotesTotalesDTO(
//                totalStock,
//                stockUltimoMes,
//                crecimiento
//        );
//    }
//
//    @Override
//    public Long obtenerLotesActivos() {
//        return loteRepos.totalLotesActivos();
//    }
//
//    @Override
//    public List<LoteMensualDTO> obtenerLotesPorMes() {
//        return loteRepos.obtenerLotesPorMes();
//    }
//
//    @Override
//    public List<LoteDetalleDTO> obtenerHistorialPrenda(Long idPrenda) {
//
//        List<Lote> lotes = loteRepos.obtenerHistorialPorPrenda(idPrenda);
//
//        return lotes.stream().map(lote -> {
//
//            LoteDetalleDTO dto = new LoteDetalleDTO();
//
//            dto.setIdLote(lote.getIdLote());
//            dto.setNumeroLote(lote.getNumeroLote());
//            dto.setCantidad(lote.getCantidad());
//            dto.setStockActual(lote.getStockActual());
//            dto.setPrecioCompraTotal(lote.getPrecioCompraTotal());
//            dto.setPrecioVenta(lote.getPrecioVenta());
//            dto.setFechaIngreso(lote.getFechaIngreso());
//            dto.setActivo(lote.getActivo());
//
//            List<HistorialDTO> historiales = lote.getInventarios()
//                    .stream()
//                    .sorted((a, b) -> b.getIdInventario().compareTo(a.getIdInventario()))
//                    .map(inv -> {
//                        HistorialDTO histDTO = new HistorialDTO();
//
//                        histDTO.setIdInventario(inv.getIdInventario());
//                        histDTO.setStock(inv.getStock());
//
//                        Talla talla = inv.getTalla();
//                        TallaDTO tallaDTO = new TallaDTO();
//                        tallaDTO.setIdTalla(talla.getIdTalla());
//                        tallaDTO.setNombre(talla.getNombre());
//
//                        histDTO.setTalla(tallaDTO);
//
//                        return histDTO;
//                    }).toList();
//
//            dto.setHistoriales(historiales);
//
//            return dto;
//
//        }).toList();
//    }
}
