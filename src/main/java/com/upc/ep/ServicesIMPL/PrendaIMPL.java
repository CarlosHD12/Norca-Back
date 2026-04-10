package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.*;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.PrendaService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public PrendaDTO registrarPrenda(PrendaDTO prendaDTO) {
        Prenda prenda = new Prenda();
        prenda.setMaterial(prendaDTO.getMaterial());
        prenda.setDescripcion(prendaDTO.getDescripcion());
        prenda.setColores(prendaDTO.getColores() != null ? prendaDTO.getColores() : new ArrayList<>());
        prenda.setFechaRegistro(LocalDate.now());
        prenda.setEstado("SIN LOTES");
        prenda.setCategoria(categoriaRepos.findById(
                        prendaDTO.getCategoria().getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"))
        );
        prenda.setMarca(marcaRepos.findById(
                        prendaDTO.getMarca().getIdMarca())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"))
        );
        Prenda guardada = prendaRepos.save(prenda);
        prendaDTO.setIdPrenda(guardada.getIdPrenda());
        prendaDTO.setEstado(guardada.getEstado());
        return prendaDTO;
    }

    @Override
    @Transactional
    public PrendaDTO editarPrenda(Long id, PrendaDTO prendaDTO) {
        Prenda prenda = prendaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        prenda.setMaterial(prendaDTO.getMaterial());
        prenda.setDescripcion(prendaDTO.getDescripcion());
        prenda.setColores(prendaDTO.getColores() != null ? prendaDTO.getColores() : new ArrayList<>());
        if (prendaDTO.getEstado() != null) {
            prenda.setEstado(prendaDTO.getEstado());
        }
        if (!prenda.getCategoria().getIdCategoria().equals(prendaDTO.getCategoria().getIdCategoria())) {
            prenda.setCategoria(
                    categoriaRepos.findById(prendaDTO.getCategoria().getIdCategoria())
                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"))
            );
        }
        if (!prenda.getMarca().getIdMarca().equals(prendaDTO.getMarca().getIdMarca())) {
            prenda.setMarca(
                    marcaRepos.findById(prendaDTO.getMarca().getIdMarca())
                            .orElseThrow(() -> new RuntimeException("Marca no encontrada"))
            );
        }
        Prenda guardada = prendaRepos.save(prenda);
        return new PrendaDTO(
                guardada.getIdPrenda(),
                guardada.getMaterial(),
                guardada.getFechaRegistro(),
                guardada.getEstado(),
                guardada.getDescripcion(),
                guardada.getColores(),
                new CategoriaDTO(
                        guardada.getCategoria().getIdCategoria(),
                        guardada.getCategoria().getNombre()
                ),
                new MarcaDTO(
                        guardada.getMarca().getIdMarca(),
                        guardada.getMarca().getNombre()
                )
        );
    }

    @Override
    @Transactional
    public void eliminarPrenda(Long id) {
        Prenda prenda = prendaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        prendaRepos.delete(prenda);
    }

    @Override
    @Transactional
    public Prenda obtenerPrendaPorId(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con id: " + idPrenda));
        prenda.getColores().size();
        prenda.getCategoria().getNombre();
        prenda.getMarca().getNombre();
        prenda.getLotes().size();
        if (prenda.getMetrica() != null) prenda.getMetrica().getIdMetrica();

        return prenda;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrendaListadoDTO> listarPrendasConStockYUltimoPrecio() {

        List<Prenda> prendas = prendaRepos.findAllPrendasOrdenDesc();
        List<PrendaListadoDTO> listado = new ArrayList<>();
        for (Prenda p : prendas) {

            Long categoriaId = p.getCategoria() != null ? p.getCategoria().getIdCategoria() : null;
            String categoriaNombre = p.getCategoria() != null ? p.getCategoria().getNombre() : "N/A";

            Long marcaId = p.getMarca() != null ? p.getMarca().getIdMarca() : null;
            String marcaNombre = p.getMarca() != null ? p.getMarca().getNombre() : "N/A";
            Lote ultimoLote = loteRepos
                    .findTopByPrendaIdPrendaAndActivoTrueOrderByFechaIngresoDescIdLoteDesc(p.getIdPrenda());
            int stockActual = 0;
            double precioUltimo = 0.0;
            if (ultimoLote != null) {
                stockActual = ultimoLote.getStockActual();
                precioUltimo = ultimoLote.getPrecioVenta();
            }
            PrendaListadoDTO dto = new PrendaListadoDTO(
                    p.getIdPrenda(),
                    categoriaId,
                    categoriaNombre,
                    marcaId,
                    marcaNombre,
                    p.getMaterial(),
                    p.getDescripcion(),
                    p.getEstado(),
                    stockActual,
                    precioUltimo,
                    p.getColores()
            );
            listado.add(dto);
        }
        return listado;
    }

    @Override
    @Transactional(readOnly = true)
    public PrendaDetalleDTO obtenerDetallePrenda(Long idPrenda) {
        Prenda prenda = prendaRepos.obtenerDetallePrenda(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada"));
        Lote ultimoLote = prenda.getLotes().stream()
                .max(Comparator
                        .comparing(Lote::getFechaIngreso)
                        .thenComparing(Lote::getIdLote))
                .orElse(null);

        LoteDetalleDTO loteDTO = null;

        if (ultimoLote != null) {
            List<HistorialDTO> historiales = ultimoLote.getInventarios().stream()
                    .sorted((a, b) -> b.getIdInventario().compareTo(a.getIdInventario()))
                    .map(inv -> {

                        HistorialDTO dtoHist = new HistorialDTO();

                        dtoHist.setIdInventario(inv.getIdInventario());
                        dtoHist.setStock(inv.getStock());

                        Talla talla = inv.getTalla();
                        TallaDTO tallaDTO = new TallaDTO();
                        tallaDTO.setIdTalla(talla.getIdTalla());
                        tallaDTO.setNombre(talla.getNombre());

                        dtoHist.setTalla(tallaDTO);

                        return dtoHist;
                    })
                    .toList();
            loteDTO = new LoteDetalleDTO();
            loteDTO.setIdLote(ultimoLote.getIdLote());
            loteDTO.setNumeroLote(ultimoLote.getNumeroLote());
            loteDTO.setCantidad(ultimoLote.getCantidad());
            loteDTO.setStockActual(ultimoLote.getStockActual());
            loteDTO.setPrecioCompraTotal(ultimoLote.getPrecioCompraTotal());
            loteDTO.setPrecioVenta(ultimoLote.getPrecioVenta());
            loteDTO.setFechaIngreso(ultimoLote.getFechaIngreso());
            loteDTO.setActivo(ultimoLote.getActivo());
            loteDTO.setHistoriales(historiales);
        }
        PrendaDetalleDTO dto = new PrendaDetalleDTO();
        dto.setIdPrenda(prenda.getIdPrenda());
        dto.setMaterial(prenda.getMaterial());
        dto.setFechaRegistro(prenda.getFechaRegistro());
        dto.setEstado(prenda.getEstado());
        dto.setDescripcion(prenda.getDescripcion());
        dto.setColores(prenda.getColores());
        dto.setNombreCategoria(prenda.getCategoria().getNombre());

        if (prenda.getCategoria().getImagenBytes() != null) {
            String base64 = Base64.getEncoder()
                    .encodeToString(prenda.getCategoria().getImagenBytes());
            dto.setImagen("data:image/png;base64," + base64);
        }

        dto.setNombreMarca(prenda.getMarca().getNombre());
        dto.setLoteActivo(loteDTO);

        return dto;
    }

    @Override
    @Transactional
    public void cambiarEstado(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con id: " + idPrenda));
        prenda.setEstadoAnterior(prenda.getEstado());
        prenda.setEstado("INACTIVO");
        prendaRepos.save(prenda);
    }

    @Override
    @Transactional
    public void activarPrenda(Long idPrenda) {
        Prenda prenda = prendaRepos.findById(idPrenda)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con id: " + idPrenda));
        if (prenda.getEstadoAnterior() != null) {
            prenda.setEstado(prenda.getEstadoAnterior());
        } else {
            prenda.setEstado("SIN LOTES");
        }
        prendaRepos.save(prenda);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrendaCarritoDTO> listarPrendasDisponibles() {
        return prendaRepos.findByEstadoOrderByIdDesc("DISPONIBLE")
                .stream()
                .map(prenda -> {
                    PrendaCarritoDTO dto = new PrendaCarritoDTO();
                    dto.setIdPrenda(prenda.getIdPrenda());
                    dto.setCategoria(prenda.getCategoria().getNombre());
                    dto.setMarca(prenda.getMarca().getNombre());
                    dto.setMaterial(prenda.getMaterial());
                    dto.setDescripcion(prenda.getDescripcion());
                    if (prenda.getCategoria().getImagenBytes() != null) {
                        dto.setImagen(Base64.getEncoder()
                                .encodeToString(prenda.getCategoria().getImagenBytes()));
                    }

                    List<Lote> lotesActivos = prenda.getLotes().stream()
                            .filter(l -> Boolean.TRUE.equals(l.getActivo()) && l.getStockActual() > 0)
                            .toList();
                    Lote loteMasAntiguo = lotesActivos.stream()
                            .min(Comparator
                                    .comparing(Lote::getFechaIngreso)
                                    .thenComparing(Lote::getIdLote))
                            .orElse(null);
                    dto.setStock(
                            loteMasAntiguo != null ? loteMasAntiguo.getStockActual() : 0
                    );
                    dto.setPrecioVenta(
                            loteMasAntiguo != null ? loteMasAntiguo.getPrecioVenta() : 0.0
                    );
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioActivoDTO> listarInventarioPorPrenda(Long idPrenda) {
        List<Inventario> inventarios = prendaRepos.findInventarioActivoFIFO(idPrenda);
        if (inventarios.isEmpty()) return List.of();
        Long loteMasAntiguo = inventarios.get(0).getLote().getIdLote();
        return inventarios.stream()
                .filter(i -> i.getLote().getIdLote().equals(loteMasAntiguo))
                .map(i -> {
                    Lote l = i.getLote();
                    return new InventarioActivoDTO(
                            l.getIdLote(),
                            l.getStockActual(),
                            l.getPrecioVenta(),
                            i.getIdInventario(),
                            i.getStock(),
                            i.getTalla().getNombre()
                    );
                })
                .toList();
    }

    private List<Map<String, Object>> convertirAListaMapa(List<Object[]> resultados, String keyName) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Object[] r : resultados) {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put(keyName, r[0]);
            mapa.put("total", r[1]);
            lista.add(mapa);
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> distribucionPorCategoria() {
        return convertirAListaMapa(prendaRepos.countByCategoria(), "categoria");
    }

    @Override
    public List<Map<String, Object>> distribucionPorMarca() {
        return convertirAListaMapa(prendaRepos.countByMarca(), "marca");
    }

    @Override
    public List<Map<String, Object>> distribucionPorEstado() {
        return convertirAListaMapa(prendaRepos.countByEstado(), "estado");
    }

    @Override
    public List<PrendaOlvidadaDTO> obtenerPrendasOlvidadas() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(30);
        return prendaRepos.findPrendasOlvidadas(fechaLimite);
    }

    @Override
    public List<TopDTO> rankingPrendasMasVendidas() {
        List<Object[]> resultados = prendaRepos.rankingPrendasMasVendidas();
        List<TopDTO> lista = new ArrayList<>();
        for (Object[] r : resultados) {
            TopDTO dto = new TopDTO();
            dto.setIdPrenda((Long) r[0]);
            dto.setCategoria((String) r[1]);
            dto.setMarca((String) r[2]);
            dto.setMaterial((String) r[3]);
            dto.setDescripcion((String) r[4]);
            dto.setUnidadesVendidas((Long) r[5]);
            lista.add(dto);
        }
        return lista;
    }

    @Override
    public List<StockBajoDTO> bajoStock(Integer limite) {
        if (limite == null) {
            limite = 5;
        }
        return prendaRepos.bajoStock(limite);
    }

    @Override
    public PrendasTotalesDTO obtenerKPIPrendas() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesActual = hoy.minusMonths(1);
        LocalDate inicioMesAnterior = hoy.minusMonths(2);
        Long total = prendaRepos.totalPrendas();
        Long ultimoMes = prendaRepos.prendasDesde(inicioMesActual);
        Long mesAnterior = prendaRepos.prendasEntre(inicioMesAnterior, inicioMesActual);
        double crecimiento = 0;
        if (mesAnterior > 0) {
            crecimiento = ((double) (ultimoMes - mesAnterior) / mesAnterior) * 100;
        }

        return new PrendasTotalesDTO(
                total,
                ultimoMes,
                crecimiento
        );
    }

    @Override
    public Long obtenerPrendasAgotadas() {
        return prendaRepos.totalPrendasAgotadas();
    }

    @Override
    public List<StockCategoriaDTO> obtenerStockPorCategoria() {
        return prendaRepos.stockPorCategoria();
    }

    private PrendaDTO mapToDTO(Prenda prenda) {
        PrendaDTO dto = new PrendaDTO();
        dto.setIdPrenda(prenda.getIdPrenda());
        dto.setMaterial(prenda.getMaterial());
        dto.setDescripcion(prenda.getDescripcion());
        dto.setFechaRegistro(prenda.getFechaRegistro());
        dto.setEstado(prenda.getEstado());
        dto.setColores(prenda.getColores());
        CategoriaDTO catDTO = new CategoriaDTO();
        catDTO.setIdCategoria(prenda.getCategoria().getIdCategoria());
        catDTO.setNombre(prenda.getCategoria().getNombre());
        dto.setCategoria(catDTO);

        MarcaDTO marDTO = new MarcaDTO();
        marDTO.setIdMarca(prenda.getMarca().getIdMarca());
        marDTO.setNombre(prenda.getMarca().getNombre());
        dto.setMarca(marDTO);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrendaDTO> obtenerPrendas() {
        return prendaRepos.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}