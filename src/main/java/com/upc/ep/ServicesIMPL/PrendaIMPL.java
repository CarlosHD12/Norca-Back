package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.*;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.LoteService;
import com.upc.ep.Services.MovimientoService;
import com.upc.ep.Services.PrendaService;

import com.upc.ep.Specification.PrendaSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PrendaIMPL implements PrendaService {
    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private CategoriaRepos categoriaRepos;

    @Autowired
    private MarcaRepos marcaRepos;

    @Autowired
    private LoteRepos loteRepos;

    @Autowired
    private MetricaRepos metricaRepos;

    @Autowired
    private LoteService loteService;

    @Autowired
    private MovimientoRepos movimientoRepos;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private VentaRepos ventaRepos;

    @Override
    @Transactional
    public PrendaResponseDTO registrarPrenda(PrendaRegistroDTO dto) {
        Categoria categoria = categoriaRepos
                .findByIdCategoriaAndActivoTrue(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada o inactiva"));
        Marca marca = marcaRepos
                .findByIdMarcaAndActivoTrue(dto.getMarcaId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada o inactiva"));
        String codigo = generarCodigo();
        Prenda prenda = new Prenda();
        prenda.setCodigo(codigo);
        prenda.setNombre(dto.getNombre().trim());
        prenda.setMaterial(dto.getMaterial().trim());
        prenda.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        prenda.setImagenUrl(dto.getImagenUrl() != null ? dto.getImagenUrl().trim() : null);
        prenda.setColores(dto.getColores() != null ? dto.getColores() : new ArrayList<>());
        prenda.setCategoria(categoria);
        prenda.setMarca(marca);
        prenda.setEstado(Prenda.EstadoPrenda.SIN_LOTES);
        prenda.setActivo(true);
        Prenda prendaGuardada = prendaRepos.save(prenda);
        Metrica metrica = new Metrica();
        metrica.setPrenda(prendaGuardada);
        metrica.setUnidadesVendidas(0);
        metrica.setIngresosTotales(BigDecimal.ZERO);
        metrica.setGananciaAcumulada(BigDecimal.ZERO);
        metrica.setInversionTotal(BigDecimal.ZERO);
        metrica.setVentasRealizadas(0);
        metricaRepos.save(metrica);
        prendaGuardada.setMetrica(metrica);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.PRENDA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_PRENDA)
                        .entidadId(prenda.getIdPrenda())
                        .codigoReferencia(prenda.getCodigo())
                        .motivo("Se registró la prenda " + prenda.getCodigo())
                        .build()
        );
        return mapToResponse(prendaGuardada);
    }

    private String generarCodigo() {
        String codigo;
        do {
            codigo = "PRD-" + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
        } while (prendaRepos.existsByCodigo(codigo));
        return codigo;
    }

    private PrendaResponseDTO mapToResponse(Prenda prenda) {
        return PrendaResponseDTO.builder()
                .idPrenda(prenda.getIdPrenda())
                .codigo(prenda.getCodigo())
                .nombre(prenda.getNombre())
                .material(prenda.getMaterial())
                .descripcion(prenda.getDescripcion())
                .imagenUrl(prenda.getImagenUrl())
                .colores(prenda.getColores())
                .categoria(prenda.getCategoria().getNombre())
                .marca(prenda.getMarca().getNombre())
                .estado(prenda.getEstado().name())
                .activo(prenda.getActivo())
                .fechaRegistro(prenda.getFechaRegistro())
                .build();
    }

    @Override
    @Transactional
    public PrendaResponseDTO actualizarPrenda(Long idPrenda, PrendaUpdateDTO dto) {
        System.out.println("ENTRO A ACTUALIZAR PRENDA");
        Prenda prenda = prendaRepos.findById(idPrenda).orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        if (!prenda.getActivo()) {throw new RuntimeException("La prenda está inactiva");}
        Categoria categoria = categoriaRepos.findByIdCategoriaAndActivoTrue(dto.getCategoriaId()).orElseThrow(() -> new RuntimeException("Categoría no encontrada o inactiva"));
        Marca marca = marcaRepos.findByIdMarcaAndActivoTrue(dto.getMarcaId()).orElseThrow(() -> new RuntimeException("Marca no encontrada o inactiva"));
        prenda.setNombre(dto.getNombre().trim());
        prenda.setMaterial(dto.getMaterial().trim());
        prenda.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        prenda.setImagenUrl(dto.getImagenUrl() != null ? dto.getImagenUrl().trim() : null);
        prenda.setColores(dto.getColores() != null ? dto.getColores() : new ArrayList<>());
        prenda.setCategoria(categoria);
        prenda.setMarca(marca);
        Prenda prendaActualizada = prendaRepos.save(prenda);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.PRENDA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.MODIFICACION_PRENDA)
                        .entidadId(prenda.getIdPrenda())
                        .codigoReferencia(prenda.getCodigo())
                        .motivo("Se actualizó la información de la prenda " + prenda.getCodigo())
                        .build()
        );
        return mapToResponse(prendaActualizada);
    }

    @Override
    @Transactional
    public void inhabilitarPrenda(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda).orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        if (!prenda.getActivo()) {throw new RuntimeException("La prenda ya se encuentra inactiva");}
        prenda.setEstadoAnterior(prenda.getEstado());
        prenda.setEstado(Prenda.EstadoPrenda.INHABILITADA);
        prenda.setActivo(false);
        prendaRepos.save(prenda);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.PRENDA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.INHABILITACION_PRENDA)
                        .entidadId(prenda.getIdPrenda())
                        .codigoReferencia(prenda.getCodigo())
                        .motivo("Se inhabilitó la prenda " + prenda.getCodigo())
                        .build()
        );
    }

    @Override
    public PrendaDetalleDTO obtenerDetallePrenda(Long idPrenda) {

        Prenda prenda = prendaRepos
                .findDetalleByIdPrenda(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));

        Optional<Lote> loteFIFO = prenda.getLotes()
                .stream()
                .filter(Lote::getActivo)
                .filter(l -> l.getStockActual() > 0)
                .min(Comparator.comparing(Lote::getFechaIngreso));

        PrendaDetalleDTO.PrendaDetalleDTOBuilder dto = PrendaDetalleDTO.builder()
                .idPrenda(prenda.getIdPrenda())
                .codigo(prenda.getCodigo())
                .nombre(prenda.getNombre())
                .imagenUrl(prenda.getImagenUrl())
                .categoria(prenda.getCategoria().getNombre())
                .marca(prenda.getMarca().getNombre())
                .estado(prenda.getEstado().name())
                .fechaRegistro(prenda.getFechaRegistro())
                .material(prenda.getMaterial())
                .descripcion(prenda.getDescripcion())
                .colores(prenda.getColores())
                .inventarios(List.of());

        if (loteFIFO.isPresent()) {

            Lote lote = loteFIFO.get();

            List<InventarioHistorialDTO> inventarios =
                    lote.getInventarios()
                            .stream()
                            .map(inv -> InventarioHistorialDTO.builder()
                                    .idInventario(inv.getIdInventario())
                                    .talla(inv.getTalla().getNombre())
                                    .stock(inv.getStock())
                                    .build())
                            .toList();

            ResumenLoteDTO resumen =
                    loteService.obtenerResumenLote(lote.getIdLote());

            dto.idLote(lote.getIdLote())
                    .codigoLote(lote.getCodigoLote())
                    .cantidadInicial(lote.getCantidadInicial())
                    .stockActual(lote.getStockActual())
                    .precioVenta(lote.getPrecioVenta())
                    .precioCompra(lote.getPrecioCompraTotal())
                    .fechaIngreso(lote.getFechaIngreso())
                    .inventarios(inventarios)
                    .resumen(resumen);
        }

        return dto.build();
    }

    @Override
    @Transactional
    public void validarPrendaAgotada(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda).orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        if (prenda.getEstado() == Prenda.EstadoPrenda.INHABILITADA) {
            return;
        }
        boolean tieneLotesDisponibles = prenda.getLotes()
                .stream()
                .anyMatch(lote -> Boolean.TRUE.equals(lote.getActivo()) && lote.getStockActual() > 0);
        Prenda.EstadoPrenda estadoActual = prenda.getEstado();
        if (tieneLotesDisponibles) {
            if (estadoActual != Prenda.EstadoPrenda.DISPONIBLE) {
                prenda.setEstadoAnterior(estadoActual);
                prenda.setEstado(Prenda.EstadoPrenda.DISPONIBLE);
                prendaRepos.save(prenda);
                movimientoService.registrarMovimiento(
                        MovimientoRegistroDTO.builder()
                                .modulo(Movimiento.ModuloMovimiento.PRENDA)
                                .tipoMovimiento(Movimiento.TipoMovimiento.PRENDA_DISPONIBLE)
                                .entidadId(prenda.getIdPrenda())
                                .codigoReferencia(prenda.getCodigo())
                                .motivo("La prenda " + prenda.getCodigo() + " volvió a estar disponible para la venta")
                                .build()
                );
            }
        } else {
            if (estadoActual != Prenda.EstadoPrenda.AGOTADO) {
                prenda.setEstadoAnterior(estadoActual);
                prenda.setEstado(Prenda.EstadoPrenda.AGOTADO);
                prendaRepos.save(prenda);
                movimientoService.registrarMovimiento(
                        MovimientoRegistroDTO.builder()
                                .modulo(Movimiento.ModuloMovimiento.PRENDA)
                                .tipoMovimiento(Movimiento.TipoMovimiento.PRENDA_AGOTADA)
                                .entidadId(prenda.getIdPrenda())
                                .codigoReferencia(prenda.getCodigo())
                                .motivo("La prenda " + prenda.getCodigo() + " se quedó sin stock disponible")
                                .build()
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrendaListResponseDTO> listarPrendasFiltradas(
            String search,
            String categoria,
            String marca,
            String estado,
            Integer stockMin,
            Integer stockMax,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Pageable pageable
    ) {
        Specification<Prenda> specification =
                PrendaSpecification.filtrarPrendas(
                        search,
                        categoria,
                        marca,
                        estado,
                        stockMin,
                        stockMax,
                        precioMin,
                        precioMax
                );
        Page<Prenda> prendas =
                prendaRepos.findAll(
                        specification,
                        pageable
                );
        return prendas.map(this::toListDTO);
    }

    private PrendaListResponseDTO toListDTO(Prenda prenda) {
        Optional<Lote> loteFIFO = prenda.getLotes()
                .stream()
                .filter(Lote::getActivo)
                .filter(l -> l.getStockActual() > 0)
                .min(Comparator.comparing(Lote::getFechaIngreso));
        Integer stockTotal = prenda.getLotes()
                .stream()
                .filter(Lote::getActivo)
                .mapToInt(Lote::getStockActual)
                .sum();
        return PrendaListResponseDTO.builder()
                .idPrenda(prenda.getIdPrenda())
                .categoriaId(prenda.getCategoria().getIdCategoria())
                .marcaId(prenda.getMarca().getIdMarca())
                .codigo(prenda.getCodigo())
                .imagenUrl(prenda.getImagenUrl())
                .nombre(prenda.getNombre())
                .categoria(prenda.getCategoria().getNombre())
                .marca(prenda.getMarca().getNombre())
                .material(prenda.getMaterial())
                .precioVenta(loteFIFO.map(Lote::getPrecioVenta).orElse(BigDecimal.ZERO))
                .stock(stockTotal)
                .estado(prenda.getEstado().name())
                .descripcion(prenda.getDescripcion())
                .fechaRegistro(prenda.getFechaRegistro())
                .colores(prenda.getColores() != null ? new ArrayList<>(prenda.getColores()) : new ArrayList<>()).build();
    }

    @Override
    @Transactional
    public void activarPrenda(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda).orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        if (prenda.getActivo()) {throw new RuntimeException("La prenda ya se encuentra activa");}
        prenda.setActivo(true);
        if (prenda.getEstadoAnterior() != null) {
            prenda.setEstado(prenda.getEstadoAnterior());
        } else {
            prenda.setEstado(Prenda.EstadoPrenda.SIN_LOTES);
        }
        prendaRepos.save(prenda);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.PRENDA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REACTIVACION_PRENDA)
                        .entidadId(prenda.getIdPrenda())
                        .codigoReferencia(prenda.getCodigo())
                        .motivo("Se reactivó la prenda " + prenda.getCodigo())
                        .build()
        );
    }

    @Override
    public PrendaKpiResponseDTO obtenerKpis() {
        LocalDateTime ultimoMes = LocalDateTime.now().minusMonths(1);
        LocalDateTime ultimaSemana = LocalDateTime.now().minusWeeks(1);
        long totalPrendas = prendaRepos.countByActivoTrue();
        long prendasUltimoMes = prendaRepos.countByFechaRegistroAfter(ultimoMes);
        long prendasDisponibles = prendaRepos.countByEstado(Prenda.EstadoPrenda.DISPONIBLE);
        long disponiblesUltimaSemana = prendaRepos.countByEstadoAndFechaRegistroAfter(Prenda.EstadoPrenda.DISPONIBLE, ultimaSemana);
        long prendasAgotadas = prendaRepos.countByEstado(Prenda.EstadoPrenda.AGOTADO);
        long agotadasUltimaSemana = movimientoRepos.countByTipoMovimientoAndFechaAfter(Movimiento.TipoMovimiento.PRENDA_AGOTADA, ultimaSemana);
        long lotesActivos = loteRepos.countByActivoTrue();
        long lotesUltimoMes = loteRepos.countByFechaIngresoAfter(ultimoMes);
        BigDecimal valorTotalInventario = loteRepos.obtenerValorTotalInventario();
        BigDecimal inversionUltimoMes = loteRepos.obtenerInversionUltimoMes(ultimoMes).setScale(2, RoundingMode.HALF_UP);
        return PrendaKpiResponseDTO.builder()
                .totalPrendas(totalPrendas)
                .prendasUltimoMes(prendasUltimoMes)
                .prendasDisponibles(prendasDisponibles)
                .disponiblesUltimaSemana(disponiblesUltimaSemana)
                .prendasAgotadas(prendasAgotadas)
                .agotadasUltimaSemana(agotadasUltimaSemana)
                .lotesActivos(lotesActivos)
                .lotesUltimoMes(lotesUltimoMes)
                .valorTotalInventario(valorTotalInventario)
                .inversionUltimoMes(inversionUltimoMes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PrendaQuickDetalleDTO obtenerDetalleRapido(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda).orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + idPrenda));        Integer stockTotal = prenda.getLotes()
                .stream()
                .filter(Lote::getActivo)
                .mapToInt(Lote::getStockActual)
                .sum();
        Integer lotesActivos = (int) prenda.getLotes()
                .stream()
                .filter(Lote::getActivo)
                .count();
        Long unidadesVendidasUltimos30Dias = ventaRepos.obtenerUnidadesVendidas(idPrenda, LocalDateTime.now().minusDays(30));
        Lote loteFIFO = prenda.getLotes()
                .stream()
                .filter(Lote::getActivo)
                .filter(l -> l.getStockActual() > 0)
                .min(Comparator.comparing(Lote::getFechaIngreso))
                .orElse(null);
        LoteFIFODTO loteFifoDTO = null;
        if (loteFIFO != null) {
            List<InventarioHistorialDTO> inventarios =
                    loteFIFO.getInventarios()
                            .stream()
                            .sorted(Comparator.comparing(inv -> inv.getTalla().getNombre()))
                            .map(inv -> InventarioHistorialDTO.builder()
                                    .idInventario(inv.getIdInventario())
                                    .talla(inv.getTalla().getNombre())
                                    .stock(inv.getStock())
                                    .build()).toList();
            loteFifoDTO = LoteFIFODTO.builder()
                    .idLote(loteFIFO.getIdLote())
                    .codigoLote(loteFIFO.getCodigoLote())
                    .activo(loteFIFO.getActivo())
                    .fechaIngreso(loteFIFO.getFechaIngreso())
                    .cantidadInicial(loteFIFO.getCantidadInicial())
                    .stockActual(loteFIFO.getStockActual())
                    .precioVenta(loteFIFO.getPrecioVenta())
                    .inventarios(inventarios)
                    .build();
        }
        return PrendaQuickDetalleDTO.builder()
                .idPrenda(prenda.getIdPrenda())
                .codigo(prenda.getCodigo())
                .nombre(prenda.getNombre())
                .imagenUrl(prenda.getImagenUrl())
                .estado(prenda.getEstado().name())
                .stockTotal(stockTotal)
                .lotesActivos(lotesActivos)
                .unidadesVendidasUltimos30Dias(unidadesVendidasUltimos30Dias)
                .loteFIFO(loteFifoDTO)
                .build();
    }

}