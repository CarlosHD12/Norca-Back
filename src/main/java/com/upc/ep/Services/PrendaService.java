package com.upc.ep.Services;

import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Entidades.Prenda;

import java.time.LocalDate;
import java.util.List;

public interface PrendaService {
    Prenda findById(Long id);
    public PrendaDTO savePrenda(Prenda prenda);
    public List<Prenda> listarPrendas();
    void actualizarEstadoPrenda(Prenda prenda);
    PrendaDTO putPrenda(Long id, PrendaDTO prendaDTO);
    public boolean eliminarPrenda(Long id);
    List<Prenda> listarPorMarca(Long idMarca);
    List<Prenda> listarPorCategoria(Long idCategoria);
    List<Prenda> listarPorCalidad(String calidad);
    List<Prenda> listarPorEstado(String estado);
    List<Marca> listarMarcas();
    List<Prenda> listarPorRangoPrecio(Double min, Double max);
    List<Prenda> listarPorFecha(LocalDate fechaRegistro);
    boolean actualizarEstado(Long id, String nuevoEstado);
    boolean verificarPrendaExistente(Long marcaId, String calidad);
    List<PrendaDTO> buscarPrendas(String descripcion, Long idMarca, Long idCategoria, String estado,
                                  LocalDate fecha, LocalDate fechaDesde, LocalDate fechaHasta);
}
