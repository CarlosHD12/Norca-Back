package com.upc.ep.Controller;

import com.upc.ep.DTO.PrendaDTO;
import com.upc.ep.Entidades.Marca;
import com.upc.ep.Entidades.Prenda;
import com.upc.ep.Repositorio.PrendaRepos;
import com.upc.ep.Services.PrendaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class PrendaController {
    @Autowired
    private PrendaService prendaService;

    @Autowired
    private PrendaRepos prendaRepos;

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/prenda")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public PrendaDTO savePrenda(@RequestBody Prenda prenda) {
        return prendaService.savePrenda(prenda);
    }

    // -------------------- LISTAR TODAS --------------------
    @GetMapping("/prendas")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPrendas() {
        return prendaService.listarPrendas().stream()
                .map(prenda -> modelMapper.map(prenda, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- EDITAR --------------------
    @PutMapping("/prenda/modificar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<PrendaDTO> putPrenda(@PathVariable Long id, @RequestBody PrendaDTO prendaDTO) {
        PrendaDTO actualizada = prendaService.putPrenda(id, prendaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    // -------------------- FILTRAR POR MARCA --------------------
    @GetMapping("/prendas/marca/{idMarca}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPorMarca(@PathVariable Long idMarca) {
        List<Prenda> prendas = prendaService.listarPorMarca(idMarca);
        return prendas.stream()
                .map(p -> modelMapper.map(p, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- DETALLES DE PRENDA ESPECIFICA --------------------
    @GetMapping("/detalle/prenda/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<PrendaDTO> getPrendaById(@PathVariable Long id) {
        Prenda prenda = prendaService.findById(id);

        // Mapear a DTO
        PrendaDTO prendaDTO = modelMapper.map(prenda, PrendaDTO.class);

        return ResponseEntity.ok(prendaDTO);
    }

    // -------------------- FILTRAR POR CATEGORÍA --------------------
    @GetMapping("/prendas/categoria/{idCategoria}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPorCategoria(@PathVariable Long idCategoria) {
        List<Prenda> prendas = prendaService.listarPorCategoria(idCategoria);
        return prendas.stream()
                .map(p -> modelMapper.map(p, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- FILTRAR POR CALIDAD --------------------
    @GetMapping("/prendas/calidad/{calidad}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPorCalidad(@PathVariable String calidad) {
        List<Prenda> prendas = prendaService.listarPorCalidad(calidad);
        return prendas.stream()
                .map(p -> modelMapper.map(p, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- FILTRAR POR ESTADO --------------------
    @GetMapping("/prendas/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPorEstado(@PathVariable String estado) {
        List<Prenda> prendas = prendaService.listarPorEstado(estado);
        return prendas.stream()
                .map(p -> modelMapper.map(p, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR TODAS LAS MARCAS --------------------
    @GetMapping("/prendas/marcas")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<Marca> listarMarcas() {
        return prendaService.listarMarcas();
    }

    // -------------------- FILTRAR POR RANGO DE PRECIO --------------------
    @GetMapping("/prendas/rango")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPorRangoPrecio(@RequestParam Double min, @RequestParam Double max) {
        List<Prenda> prendas = prendaService.listarPorRangoPrecio(min, max);
        return prendas.stream()
                .map(p -> modelMapper.map(p, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- FILTRAR POR FECHA --------------------
    @GetMapping("/prendas/fecha")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> listarPorFecha(@RequestParam LocalDate fecha) {
        List<Prenda> prendas = prendaService.listarPorFecha(fecha);
        return prendas.stream()
                .map(p -> modelMapper.map(p, PrendaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- ACTUALIZAR SOLO ESTADO --------------------
    @PatchMapping("/prenda/estado/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<Void> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        boolean ok = prendaService.actualizarEstado(id, estado);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // -------------------- ELIMINAR --------------------
    @DeleteMapping("/prenda/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarPrenda(@PathVariable Long id) {
        boolean eliminado = prendaService.eliminarPrenda(id);

        if (!eliminado) {
            return ResponseEntity.badRequest().build(); // 400 sin mensaje
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // -------------------- VERIFICAR EXISTE PRENDA --------------------
    @GetMapping("/prenda/existe")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<Boolean> existePrenda(
            @RequestParam Long marcaId,
            @RequestParam String calidad) {
        boolean existe = prendaService.verificarPrendaExistente(marcaId, calidad);
        return ResponseEntity.ok(existe);
    }

    // -------------------- FILTRO --------------------
    @GetMapping("/prendas/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<PrendaDTO> buscarPrendas(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Long idMarca,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta
    ){
        return prendaService.buscarPrendas(descripcion, idMarca, idCategoria, estado, fecha, fechaDesde, fechaHasta);
    }
}
