package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Entidades.Talla;
import com.upc.ep.Repositorio.*;
import com.upc.ep.Services.PrendaService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrendaIMPL implements PrendaService {

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private MarcaRepos marcaRepos;

    @Autowired
    private TallaRepos tallaRepos;

    @Autowired
    private Detalle_VentRepos detalle_ventRepos;

    @Autowired
    private Detalle_PedRepos detalle_pedRepos;

    @Autowired
    private ModelMapper modelMapper;

    // -----------------------------
    // Recalcular estado de la prenda
    // -----------------------------
    @Transactional
    public void recalcularEstado(Prenda prenda) {
        Integer stockTotal = tallaRepos.sumStockByPrendaId(prenda.getIdPrenda());
        if (stockTotal == null) stockTotal = 0;

        boolean pedidoPendiente = detalle_pedRepos.existsByPrendaIdAndPedidoEstado(prenda.getIdPrenda(), "Pendiente");

        if (pedidoPendiente) prenda.setEstado("Pedido");
        else if (stockTotal <= 0) prenda.setEstado("Agotado");
        else prenda.setEstado("Disponible");

        prendaRepos.save(prenda);
    }

    // -----------------------------
    // Mapear Prenda a DTO
    // -----------------------------
    private PrendaDTO mapToDTO(Prenda prenda) {
        PrendaDTO dto = new PrendaDTO();
        dto.setIdPrenda(prenda.getIdPrenda());
        dto.setColor(prenda.getColor());
        dto.setCalidad(prenda.getCalidad());
        dto.setPrecioCompra(prenda.getPrecioCompra());
        dto.setPrecioVenta(prenda.getPrecioVenta());
        dto.setStock(prenda.getStock());
        dto.setDescripcion(prenda.getDescripcion());
        dto.setEstado(prenda.getEstado());
        dto.setFechaRegistro(prenda.getFechaRegistro());

        if (prenda.getMarca() != null) {
            MarcaDTO marcaDTO = new MarcaDTO();
            marcaDTO.setIdMarca(prenda.getMarca().getIdMarca());
            marcaDTO.setMarca(prenda.getMarca().getMarca());
            dto.setMarca(marcaDTO);
        }

        if(prenda.getTallas() != null && !prenda.getTallas().isEmpty()) {
            List<TallaSimpleDTO> tallasDTO = prenda.getTallas().stream().map(t -> {
                TallaSimpleDTO td = new TallaSimpleDTO();
                td.setIdTalla(t.getIdTalla());
                td.setSize(t.getSize());
                td.setCantidad(t.getCantidad());
                return td;
            }).collect(Collectors.toList());
            dto.setTallas(tallasDTO);
        } else {
            dto.setTallas(new ArrayList<>());
        }


        return dto;
    }

    // -----------------------------
    // Guardar prenda
    // -----------------------------
    @Override
    public PrendaDTO savePrenda(Prenda prenda) {
        prenda.setFechaRegistro(LocalDate.now());
        Prenda prendaGuardada = prendaRepos.save(prenda);

        if (prenda.getTallas() != null && !prenda.getTallas().isEmpty()) {
            for (Talla talla : prenda.getTallas()) {
                talla.setPrenda(prendaGuardada);
                tallaRepos.save(talla);
            }
        }

        recalcularEstado(prendaGuardada);
        return mapToDTO(prendaGuardada);
    }

    // -----------------------------
    // Actualizar prenda
    // -----------------------------
    @Override
    public PrendaDTO putPrenda(Long id, PrendaDTO prendaDTO) {
        Prenda prenda = prendaRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con ID: " + id));

        // Actualizar campos básicos
        prenda.setColor(prendaDTO.getColor());
        prenda.setCalidad(prendaDTO.getCalidad());
        prenda.setPrecioCompra(prendaDTO.getPrecioCompra());
        prenda.setPrecioVenta(prendaDTO.getPrecioVenta());
        prenda.setDescripcion(prendaDTO.getDescripcion());

        // Actualizar marca
        if (prendaDTO.getMarca() != null && prendaDTO.getMarca().getIdMarca() != null) {
            Marca marca = marcaRepos.findById(prendaDTO.getMarca().getIdMarca())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
            prenda.setMarca(marca);
        }

        // Guardar cambios básicos
        Prenda prendaGuardada = prendaRepos.save(prenda);

        // Actualizar o agregar tallas
        if (prendaDTO.getTallas() != null) {
            for (TallaSimpleDTO tallaDTO : prendaDTO.getTallas()) {
                Talla tallaExistente = null;
                if (tallaDTO.getIdTalla() != null) {
                    tallaExistente = tallaRepos.findById(tallaDTO.getIdTalla()).orElse(null);
                }

                if (tallaExistente != null) {
                    tallaExistente.setCantidad(tallaDTO.getCantidad());
                } else {
                    Talla nuevaTalla = new Talla();
                    nuevaTalla.setSize(tallaDTO.getSize().trim().toUpperCase());
                    nuevaTalla.setCantidad(tallaDTO.getCantidad());
                    nuevaTalla.setPrenda(prendaGuardada);
                    prendaGuardada.getTallas().add(nuevaTalla);
                }
            }
            // Guardar todas las tallas de una vez
            tallaRepos.saveAll(prendaGuardada.getTallas());
        }

        // Recalcular stock total y estado
        Integer nuevoStock = prendaGuardada.getTallas().stream()
                .mapToInt(Talla::getCantidad).sum();
        prendaGuardada.setStock(nuevoStock);
        prendaGuardada.setEstado(nuevoStock > 0 ? "Disponible" : "Agotado");

        prendaRepos.save(prendaGuardada);

        return mapToDTO(prendaGuardada);
    }

    private void actualizarTallas(Prenda prenda, List<TallaSimpleDTO> dtos) {
        List<Talla> tallasActuales = prenda.getTallas();
        List<Talla> tallasNuevas = new ArrayList<>();

        if (dtos != null) {
            for (TallaSimpleDTO dto : dtos) {
                Talla tallaExistente = null;

                // Buscar por ID
                if (dto.getIdTalla() != null) {
                    tallaExistente = tallasActuales.stream()
                            .filter(t -> dto.getIdTalla().equals(t.getIdTalla()))
                            .findFirst()
                            .orElse(null);
                }

                // Si no se encontró por ID, buscar por size
                if (tallaExistente == null && dto.getSize() != null) {
                    tallaExistente = tallasActuales.stream()
                            .filter(t -> dto.getSize().equals(t.getSize()))
                            .findFirst()
                            .orElse(null);
                }

                if (tallaExistente != null) {
                    // Actualizar talla existente
                    tallaExistente.setSize(dto.getSize());
                    tallaExistente.setCantidad(dto.getCantidad());
                    tallasNuevas.add(tallaExistente);
                } else {
                    // Crear nueva talla
                    Talla nueva = new Talla();
                    nueva.setSize(dto.getSize());
                    nueva.setCantidad(dto.getCantidad());
                    nueva.setPrenda(prenda);
                    tallasNuevas.add(nueva);
                }
            }
        }

        // Limpiar la colección existente y añadir las actualizadas
        tallasActuales.clear();
        tallasActuales.addAll(tallasNuevas);
    }

    // -----------------------------
    // Métodos de listado y eliminación
    // -----------------------------
    @Override
    public List<Prenda> listarPrendas() {
        return prendaRepos.findAll();
    }

    @Override
    public Prenda findById(Long id) {
        return prendaRepos.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Prenda no encontrada con ID: " + id));
    }

    @Override
    public boolean eliminarPrenda(Long idPrenda) {
        Optional<Prenda> prendaOpt = prendaRepos.findById(idPrenda);
        if (prendaOpt.isEmpty()) return false;

        boolean enVenta = detalle_ventRepos.existsByPrenda_IdPrenda(idPrenda);
        boolean enPedido = detalle_pedRepos.existsByPrenda_IdPrenda(idPrenda);
        if (enVenta || enPedido) return false;

        prendaRepos.delete(prendaOpt.get());
        return true;
    }

    @Override
    public List<Prenda> listarPorMarca(Long idMarca) {
        return prendaRepos.listarPorMarca(idMarca);
    }

    @Override
    public List<Prenda> listarPorCategoria(Long idCategoria) {
        return prendaRepos.listarPorCategoria(idCategoria);
    }

    @Override
    public List<Prenda> listarPorCalidad(String calidad) {
        return prendaRepos.listarPorCalidad(calidad);
    }

    @Override
    public List<Prenda> listarPorEstado(String estado) {
        return prendaRepos.listarPorEstado(estado);
    }

    @Override
    public List<Marca> listarMarcas() {
        return prendaRepos.listarMarcas();
    }

    @Override
    public List<Prenda> listarPorRangoPrecio(Double min, Double max) {
        return prendaRepos.findByPrecioVentaBetween(min, max);
    }

    @Override
    public List<Prenda> listarPorFecha(LocalDate fechaRegistro) {
        return prendaRepos.findByFechaRegistro(fechaRegistro);
    }

    @Override
    public boolean actualizarEstado(Long id, String nuevoEstado) {
        Optional<Prenda> optPrenda = prendaRepos.findById(id);
        if (optPrenda.isEmpty()) return false;

        Prenda prenda = optPrenda.get();
        prenda.setEstado(nuevoEstado);
        prendaRepos.save(prenda);
        return true;
    }

    @Override
    public boolean verificarPrendaExistente(Long marcaId, String calidad) {
        return prendaRepos.existsByMarca_IdMarcaAndCalidad(marcaId, calidad);
    }

    @Override
    public List<PrendaDTO> buscarPrendas(String descripcion, Long idMarca, Long idCategoria, String estado,
                                         LocalDate fecha, LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Prenda> prendas = prendaRepos.buscarPrendas(descripcion, idMarca, idCategoria, estado, fecha, fechaDesde, fechaHasta);
        return prendas.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void actualizarEstadoPrenda(Prenda prenda) {
        Integer stockTotal = tallaRepos.sumStockByPrendaId(prenda.getIdPrenda());
        if (stockTotal == null) stockTotal = 0;

        boolean tienePedidoPendiente = detalle_pedRepos.existsByPrendaIdAndPedidoEstado(
                prenda.getIdPrenda(), "Pendiente"
        );

        if (tienePedidoPendiente) {
            prenda.setEstado("Pedido");
        } else if (stockTotal == 0) {
            prenda.setEstado("Agotado");
        } else {
            prenda.setEstado("Disponible");
        }

        prendaRepos.save(prenda);
    }

    public void actualizarEstadoPrendaSegunPedidos(Prenda prenda) {
        boolean enPedidoPendiente = detalle_pedRepos.existePedidoPendientePorPrenda(prenda.getIdPrenda());

        if (enPedidoPendiente) {
            prenda.setEstado("Pedido");
        } else {
            int stockTotal = tallaRepos.sumStockByPrendaId(prenda.getIdPrenda());
            prenda.setEstado(stockTotal > 0 ? "Disponible" : "Agotado");
        }

        prendaRepos.save(prenda);
    }
}
