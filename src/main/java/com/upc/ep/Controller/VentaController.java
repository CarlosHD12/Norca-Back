package com.upc.ep.Controller;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Venta;
import com.upc.ep.Services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = {
                "http://localhost:4200",
                "https://norca-back-production.up.railway.app"
        },
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping("/post/venta")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<VentaDTO> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        VentaDTO nuevaVenta = ventaService.registrarVenta(ventaDTO);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    @GetMapping("/get/venta")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<List<VentaDTO>> listarVentas() {
        List<VentaDTO> lista = ventaService.listarVentas();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @PutMapping("/put/venta/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<VentaDTO> editarVenta(@PathVariable Long id, @RequestBody VentaDTO ventaDTO) {
        VentaDTO actualizado = ventaService.editarVenta(id, ventaDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/delete/venta/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/detalle/venta/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public VentaDetalleDTO getDetalle(@PathVariable Long id) {
        return ventaService.obtenerDetalleVenta(id);
    }

    @PutMapping("/desactivar/venta/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public String desactivarVenta(@PathVariable("id") Long idVenta) {
        ventaService.desactivarVenta(idVenta);
        return "Venta desactivada correctamente";
    }

    @GetMapping("/ventas/totales")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public VentasTotalesDTO kpiVentas(){
        return ventaService.obtenerKpiVentas();
    }

    @GetMapping("/unidades/totales")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public VentasTotalesDTO kpiUnidades(){
        return ventaService.obtenerKpiUnidades();
    }

    @GetMapping("/ingresos/totales")
    @PreAuthorize("hasAnyRole('ADMIN', 'AYUDANTE')")
    public VentasTotalesDTO obtenerIngresosTotales(){
        return ventaService.obtenerIngresosTotales();
    }

    @GetMapping("/metodo/top")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public MetodoPagoDTO obtenerMetodoPagoFavorito(){
        return ventaService.obtenerMetodoPagoFavorito();
    }

    @GetMapping("/ganancias/categoria")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<IngresosCategoriaDTO> obtenerIngresosPorCategoria() {
        return ventaService.obtenerIngresosPorCategoria();
    }

    @GetMapping("/ventas/mensuales")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public List<Map<String, Object>> reporteMensual(
            @RequestParam String tipo
    ) {
        return ventaService.reportePorMes(tipo);
    }

    @GetMapping("/listar/ventas")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public Page<VentaListadoDTO> listarVentas(

            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String periodo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,

            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Integer unidadesMin,
            @RequestParam(required = false) Integer unidadesMax,

            @PageableDefault(size = 10, sort = "idVenta", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {

        codigo = (codigo == null || codigo.isBlank()) ? "" : codigo;
        metodoPago = (metodoPago == null || metodoPago.isBlank()) ? "" : metodoPago;
        periodo = (periodo == null) ? "" : periodo;

        precioMin = (precioMin == null) ? 0.0 : precioMin;
        precioMax = (precioMax == null) ? Double.MAX_VALUE : precioMax;

        unidadesMin = (unidadesMin == null) ? 0 : unidadesMin;
        unidadesMax = (unidadesMax == null) ? Integer.MAX_VALUE : unidadesMax;

        LocalDateTime fechaInicio;
        LocalDateTime fechaFin;

        if (fecha != null) {
            fechaInicio = fecha.atStartOfDay();
            fechaFin = fecha.plusDays(1).atStartOfDay();
        } else {
            fechaInicio = LocalDateTime.of(1900, 1, 1, 0, 0);
            fechaFin = LocalDateTime.of(2100, 1, 1, 0, 0);
        }

        return ventaService.listarVentas(
                codigo,
                metodoPago,
                periodo,
                fechaInicio,
                fechaFin,
                precioMin,
                precioMax,
                unidadesMin,
                unidadesMax,
                pageable
        );
    }

    @GetMapping("/impacto/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AYUDANTE')")
    public ResponseEntity<ImpactoVentaDTO> verImpacto(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerImpactoVenta(id));
    }
}