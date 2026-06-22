package com.upc.ep.ServicesIMPL;

import com.upc.ep.DTO.CategoriaRegistroDTO;
import com.upc.ep.DTO.CategoriaResponseDTO;
import com.upc.ep.DTO.CategoriaUpdateDTO;
import com.upc.ep.DTO.MovimientoRegistroDTO;
import com.upc.ep.Entidades.Categoria;
import com.upc.ep.Entidades.Movimiento;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.CategoriaRepos;
import com.upc.ep.Services.CategoriaService;
import com.upc.ep.Services.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaIMPL implements CategoriaService {
    @Autowired
    private CategoriaRepos categoriaRepos;

    @Autowired
    private MovimientoService movimientoService;

    @Override
    @Transactional
    public CategoriaResponseDTO registrarCategoria(CategoriaRegistroDTO dto) {
        if (categoriaRepos.existsByNombre(dto.getNombre())) {throw new RuntimeException("Ya existe una categoría con ese nombre");}
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setActivo(true);
        Categoria categoriaGuardada = categoriaRepos.save(categoria);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.CATEGORIA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REGISTRO_CATEGORIA)
                        .entidadId(categoria.getIdCategoria())
                        .codigoReferencia(categoria.getNombre())
                        .motivo("Se registró la categoría " + categoria.getNombre())
                        .build()
        );
        return mapToResponse(categoriaGuardada);
    }

    private CategoriaResponseDTO mapToResponse(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .activo(categoria.getActivo())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        List<Categoria> categorias = categoriaRepos.findAllByOrderByIdCategoriaDesc();
        return categorias.stream().map(this::toCategoriaDTO).toList();
    }

    private CategoriaResponseDTO toCategoriaDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .activo(categoria.getActivo())
                .build();
    }

    @Override
    @Transactional
    public CategoriaResponseDTO editarCategoria(Long idCategoria, CategoriaUpdateDTO dto) {
        Categoria categoria = categoriaRepos.findById(idCategoria).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        boolean existeNombre = categoriaRepos.existsByNombreIgnoreCase(dto.getNombre());
        if (existeNombre && !categoria.getNombre().equalsIgnoreCase(dto.getNombre())) {throw new RuntimeException("Ya existe una categoría con ese nombre");}
        categoria.setNombre(dto.getNombre());
        categoriaRepos.save(categoria);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.CATEGORIA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.MODIFICACION_CATEGORIA)
                        .entidadId(categoria.getIdCategoria())
                        .codigoReferencia(categoria.getNombre())
                        .motivo("Se actualizó la categoría " + categoria.getNombre())
                        .build()
        );
        return toCategoriaDTO(categoria);
    }

    @Override
    @Transactional
    public void desactivarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepos.findById(idCategoria).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        if (!categoria.getActivo()) {throw new RuntimeException("La categoría ya está desactivada");}
        boolean tienePrendasActivas =
                categoria.getPrendas()
                        .stream()
                        .anyMatch(Prenda::getActivo);
        if (tienePrendasActivas) {throw new RuntimeException("No se puede desactivar una categoría con prendas activas");}
        categoria.setActivo(false);
        categoriaRepos.save(categoria);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.CATEGORIA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.INHABILITACION_CATEGORIA)
                        .entidadId(categoria.getIdCategoria())
                        .codigoReferencia(categoria.getNombre())
                        .motivo("Se inhabilitó la categoría " + categoria.getNombre())
                        .build()
        );
    }

    @Override
    @Transactional
    public void activarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepos.findById(idCategoria).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        if (categoria.getActivo()) {throw new RuntimeException("La categoría ya está activa");}
        categoria.setActivo(true);
        categoriaRepos.save(categoria);
        movimientoService.registrarMovimiento(
                MovimientoRegistroDTO.builder()
                        .modulo(Movimiento.ModuloMovimiento.CATEGORIA)
                        .tipoMovimiento(Movimiento.TipoMovimiento.REACTIVACION_CATEGORIA)
                        .entidadId(categoria.getIdCategoria())
                        .codigoReferencia(categoria.getNombre())
                        .motivo("Se reactivó la categoría " + categoria.getNombre())
                        .build()
        );
    }
}