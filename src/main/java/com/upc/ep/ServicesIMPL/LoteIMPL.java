package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Inventario;
import com.upc.ep.Entidades.Lote;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Repositorio.InventarioRepos;
import com.upc.ep.Repositorio.LoteRepos;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Repositorio.TallaRepos;
import com.upc.ep.Services.LoteService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public LoteDTO registrarLote(LoteDTO loteDTO) {
        Prenda prenda = prendaRepos.findById(loteDTO.getPrenda().getIdPrenda())
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        if (loteDTO.getInventarios() == null || loteDTO.getInventarios().isEmpty()) {
            throw new RuntimeException("El lote debe tener al menos un inventario");
        }
        Lote lote = new Lote();
        lote.setPrenda(prenda);
        lote.setCantidad(loteDTO.getCantidad());
        lote.setStockActual(loteDTO.getCantidad());
        lote.setPrecioCompraTotal(loteDTO.getPrecioCompraTotal());
        lote.setPrecioVenta(loteDTO.getPrecioVenta());
        lote.setFechaIngreso(LocalDate.now());
        lote.setActivo(true);

        Integer ultimoNumero = loteRepos.findMaxNumeroLoteByPrendaIdForUpdate(prenda.getIdPrenda());

        int nuevoNumero = (ultimoNumero == null) ? 1 : ultimoNumero + 1;
        lote.setNumeroLote(nuevoNumero);

        Lote nuevoLote = loteRepos.save(lote);

        int stockAsignado = 0;

        for (InventarioDTO invDTO : loteDTO.getInventarios()) {

            if (invDTO.getStock() == null || invDTO.getStock() <= 0) {
                throw new RuntimeException("El stock del inventario debe ser mayor a 0");
            }

            Talla talla = tallaRepos.findById(invDTO.getTalla().getIdTalla())
                    .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

            if (stockAsignado + invDTO.getStock() > loteDTO.getCantidad()) {
                throw new RuntimeException("El stock total de inventarios no puede superar la cantidad del lote");
            }

            Inventario inv = new Inventario();
            inv.setLote(nuevoLote);
            inv.setTalla(talla);
            inv.setStock(invDTO.getStock());
            inventarioRepos.save(inv);
            stockAsignado += invDTO.getStock();
        }
        if (stockAsignado != loteDTO.getCantidad()) {
            throw new RuntimeException("El stock total de inventarios debe ser igual a la cantidad del lote");
        }
        prenda.setEstado("DISPONIBLE");
        prendaRepos.save(prenda);
        LoteDTO resultado = new LoteDTO();
        resultado.setIdLote(nuevoLote.getIdLote());
        resultado.setNumeroLote(nuevoLote.getNumeroLote());
        resultado.setCantidad(nuevoLote.getCantidad());
        resultado.setStockActual(nuevoLote.getStockActual());
        resultado.setPrecioCompraTotal(nuevoLote.getPrecioCompraTotal());
        resultado.setPrecioVenta(nuevoLote.getPrecioVenta());
        resultado.setFechaIngreso(nuevoLote.getFechaIngreso());
        resultado.setActivo(nuevoLote.getActivo());
        resultado.setPrenda(loteDTO.getPrenda());
        resultado.setInventarios(loteDTO.getInventarios());

        return resultado;
    }

    @Override
    public LoteMetricasDTO calcularMetricas(Long idLote) {
        Lote lote = loteRepos.findById(idLote)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        int cantidad = lote.getCantidad();

        double costoPorUnidad = lote.getPrecioCompraTotal() / (double) cantidad;

        double ventaTotal = lote.getPrecioVenta() * cantidad;

        double gananciaPorUnidad = lote.getPrecioVenta() - costoPorUnidad;

        double gananciaTotal = gananciaPorUnidad * cantidad;

        double margenGanancia = (gananciaPorUnidad / lote.getPrecioVenta()) * 100;

        double radioInversion = ventaTotal / lote.getPrecioCompraTotal();

        double puntoEquilibrio = lote.getPrecioCompraTotal() / gananciaPorUnidad;

        LoteMetricasDTO dto = new LoteMetricasDTO();
        dto.setIdLote(idLote);
        dto.setVentaTotal(ventaTotal);
        dto.setGananciaPorUnidad(gananciaPorUnidad);
        dto.setGananciaTotal(gananciaTotal);
        dto.setMargenGanancia(margenGanancia);
        dto.setRadioInversion(radioInversion);
        dto.setPuntoEquilibrio(puntoEquilibrio);

        return dto;
    }

    @Override
    public LotesTotalesDTO obtenerStockDisponible() {
        Long totalStock = loteRepos.totalStockDisponible().longValue();
        LocalDate inicioMesActual = LocalDate.now().withDayOfMonth(1);
        LocalDate inicioMesAnterior = inicioMesActual.minusMonths(1);
        Long stockUltimoMes = loteRepos
                .stockUltimoMes(inicioMesActual)
                .longValue();
        Long stockMesAnterior = loteRepos
                .stockMesAnterior(inicioMesAnterior, inicioMesActual)
                .longValue();
        double crecimiento = 0;
        if (stockMesAnterior > 0) {
            crecimiento = ((double)(stockUltimoMes - stockMesAnterior) / stockMesAnterior) * 100;
        }
        return new LotesTotalesDTO(
                totalStock,
                stockUltimoMes,
                crecimiento
        );
    }

    @Override
    public Long obtenerLotesActivos() {
        return loteRepos.totalLotesActivos();
    }

    @Override
    public List<LoteMensualDTO> obtenerLotesPorMes() {
        return loteRepos.obtenerLotesPorMes();
    }

    @Override
    public List<LoteDetalleDTO> obtenerHistorialPrenda(Long idPrenda) {

        List<Lote> lotes = loteRepos.obtenerHistorialPorPrenda(idPrenda);

        return lotes.stream().map(lote -> {

            LoteDetalleDTO dto = new LoteDetalleDTO();

            dto.setIdLote(lote.getIdLote());
            dto.setNumeroLote(lote.getNumeroLote());
            dto.setCantidad(lote.getCantidad());
            dto.setStockActual(lote.getStockActual());
            dto.setPrecioCompraTotal(lote.getPrecioCompraTotal());
            dto.setPrecioVenta(lote.getPrecioVenta());
            dto.setFechaIngreso(lote.getFechaIngreso());
            dto.setActivo(lote.getActivo());

            List<HistorialDTO> historiales = lote.getInventarios()
                    .stream()
                    .sorted((a, b) -> b.getIdInventario().compareTo(a.getIdInventario()))
                    .map(inv -> {
                        HistorialDTO histDTO = new HistorialDTO();

                        histDTO.setIdInventario(inv.getIdInventario());
                        histDTO.setStock(inv.getStock());

                        Talla talla = inv.getTalla();
                        TallaDTO tallaDTO = new TallaDTO();
                        tallaDTO.setIdTalla(talla.getIdTalla());
                        tallaDTO.setNombre(talla.getNombre());

                        histDTO.setTalla(tallaDTO);

                        return histDTO;
                    }).toList();

            dto.setHistoriales(historiales);

            return dto;

        }).toList();
    }
}
