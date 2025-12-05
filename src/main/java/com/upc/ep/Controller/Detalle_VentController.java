package com.upc.ep.Controller;

import com.upc.ep.DTO.*;
import com.upc.ep.Entidades.Detalle_Vent;
import com.upc.ep.Services.Detalle_VentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/Norca")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*"
)
public class Detalle_VentController {
    @Autowired
    private Detalle_VentService detalleService;

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GUARDAR --------------------
    @PostMapping("/dv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<Detalle_VentDTO> saveDetalle(@RequestBody Detalle_VentDTO detalleDTO) {
        Detalle_VentDTO detalleGuardado = detalleService.saveDetalleVenta(detalleDTO);
        return new ResponseEntity<>(detalleGuardado, HttpStatus.CREATED);
    }

    // -------------------- LISTAR TODAS --------------------
    @GetMapping("/dvs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<List<Detalle_VentDTO>> listarDetalles() {
        List<Detalle_VentDTO> detalles = detalleService.listarDetalles();
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

    // -------------------- LISTAR POR PRENDA --------------------
    @GetMapping("/venta/prendas/{idPrenda}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<List<Detalle_VentDTO>> listarPorPrenda(@PathVariable Long idPrenda) {
        List<Detalle_VentDTO> detalles = detalleService.listarPorPrenda(idPrenda);
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

    // -------------------- ELIMINAR DETALLE --------------------
    @DeleteMapping("/dv/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        boolean eliminado = detalleService.eliminarDetalle(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------- ACTUALIZAR DETALLE --------------------
    @PutMapping("/dv/modificar/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<Detalle_VentDTO> actualizarDetalle(
            @PathVariable Long id,
            @RequestBody Detalle_VentDTO detalleDTO) {
        Detalle_VentDTO actualizado = detalleService.actualizarDetalle(id, detalleDTO);
        return ResponseEntity.ok(actualizado);
    }

    // -------------------- CONTAR DETALLES POR VENTA --------------------
    @GetMapping("/dv/{idVenta}/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<Integer> contarDetallesPorVenta(@PathVariable Long idVenta) {
        Integer cantidad = detalleService.contarDetallesPorVenta(idVenta);
        return ResponseEntity.ok(cantidad);
    }

    // -------------------- LISTAR DETALLES POR VENTA --------------------
    @GetMapping("/dv/venta/{idVenta}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public ResponseEntity<List<Detalle_VentDTO>> listarPorVenta(@PathVariable Long idVenta) {
        List<Detalle_Vent> detalles = detalleService.listarPorVenta(idVenta);

        List<Detalle_VentDTO> detallesDTO = new ArrayList<>();
        for (Detalle_Vent d : detalles) {
            Detalle_VentDTO dto = new Detalle_VentDTO();
            dto.setIdDV(d.getIdDV());
            dto.setCantidad(d.getCantidad());
            dto.setPrecioUnitario(d.getPrecioUnitario());
            dto.setSubTotal(d.getCantidad() * d.getPrecioUnitario());

            // Mapear Prenda manualmente
            if (d.getPrenda() != null) {
                PrendaDTO prendaDTO = new PrendaDTO();
                prendaDTO.setIdPrenda(d.getPrenda().getIdPrenda());
                prendaDTO.setColor(d.getPrenda().getColor());
                prendaDTO.setCalidad(d.getPrenda().getCalidad());
                prendaDTO.setStock(d.getPrenda().getStock());
                prendaDTO.setPrecioCompra(d.getPrenda().getPrecioCompra());
                prendaDTO.setPrecioVenta(d.getPrenda().getPrecioVenta());
                prendaDTO.setEstado(d.getPrenda().getEstado());
                prendaDTO.setDescripcion(d.getPrenda().getDescripcion());
                prendaDTO.setFechaRegistro(d.getPrenda().getFechaRegistro());
                prendaDTO.setMarca(modelMapper.map(d.getPrenda().getMarca(), MarcaDTO.class));

                // Mapear la lista de tallas
                List<TallaSimpleDTO> tallasDTO = d.getPrenda().getTallas().stream()
                        .map(t -> modelMapper.map(t, TallaSimpleDTO.class))
                        .toList();
                prendaDTO.setTallas(tallasDTO);

                dto.setPrenda(prendaDTO);
            }

            // Mapear Venta
            if (d.getVenta() != null) {
                dto.setVenta(modelMapper.map(d.getVenta(), VentaDTO.class));
            }

            // Mapear Talla
            if (d.getTalla() != null) {
                dto.setTalla(modelMapper.map(d.getTalla(), TallaDTO.class));
            }

            detallesDTO.add(dto);
        }

        return ResponseEntity.ok(detallesDTO);
    }

    @GetMapping("/prenda/{id}/total-vendido")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public Integer totalVendido(@PathVariable Long id) {
        return detalleService.totalUnidadesVendidas(id);
    }

    @GetMapping("/prenda/{id}/ingresos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public Double ingresos(@PathVariable Long id) {
        return detalleService.ingresosTotales(id);
    }

    @GetMapping("/prenda/{id}/ventas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public Integer cantidadVentas(@PathVariable Long id) {
        return detalleService.cantidadVentas(id);
    }

    @GetMapping("/prenda/{id}/ultima-venta")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public LocalDateTime ultimaVenta(@PathVariable Long id) {
        return detalleService.ultimaVenta(id);
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public List<Object[]> ranking() {
        return detalleService.rankingPrendas();
    }

    @GetMapping("/prenda/{id}/tiempo-promedio")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AYUDANTE')")
    public Long tiempoPromedio(@PathVariable Long id) {
        return detalleService.tiempoPromedioVenta(id);
    }
}
