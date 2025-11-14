package com.upc.ep.Controller;

import com.upc.ep.DTO.VentaDTO;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Services.VentaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/venta")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public VentaDTO saveVenta(@RequestBody VentaDTO ventaDTO) {
        Venta venta = modelMapper.map(ventaDTO, Venta.class);
        venta = ventaService.saveVenta(venta);
        return modelMapper.map(venta, VentaDTO.class);
    }

    // -------------------- LISTAR TODAS --------------------
    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<VentaDTO> listarVentas() {
        return ventaService.listarVentas();
    }

    // -------------------- EDITAR --------------------
    @PutMapping("/venta/modificar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<VentaDTO> putVenta(@PathVariable Long id, @RequestBody VentaDTO ventaDTO) {
        VentaDTO actualizada = ventaService.putVenta(id, ventaDTO);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    // -------------------- LISTAR POR METODO DE PAGO --------------------
    @GetMapping("/ventas/metodo/{metodoPago}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<VentaDTO> listarPorMetodoPago(@PathVariable String metodoPago) {
        return ventaService.listarPorMetodoPago(metodoPago)
                .stream()
                .map(v -> modelMapper.map(v, VentaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR POR FECHA ESPECIFICA --------------------
    @GetMapping("/ventas/fecha/{fecha}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<VentaDTO> listarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ventaService.listarPorFecha(fecha)
                .stream()
                .map(v -> modelMapper.map(v, VentaDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- LISTAR POR RANGO DE FECHAS --------------------
    @GetMapping("/ventas/rango")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<VentaDTO> listarPorRangoFechas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ventaService.listarPorRangoFechas(inicio, fin)
                .stream()
                .map(v -> modelMapper.map(v, VentaDTO.class))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/venta/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<String> eliminarVenta(@PathVariable Long id) {
        boolean eliminado = ventaService.eliminarVenta(id);
        if (eliminado) {
            return ResponseEntity.ok("Venta eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La venta con ID " + id + " no existe");
        }
    }
}
