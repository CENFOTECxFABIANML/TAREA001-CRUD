package com.project.demo.rest.producto;

import com.project.demo.logic.entity.producto.Producto;
import com.project.demo.logic.entity.producto.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','USER')")
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return productoRepository.findById(id)
                .map(existingProducto -> {
                    existingProducto.setNombre(producto.getNombre());
                    existingProducto.setDescripcion(producto.getDescripcion());
                    return productoRepository.save(existingProducto);
                })
                .orElseGet(() -> {
                    producto.setId(id);
                    return productoRepository.save(producto);
                });
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public Producto addProducto(@RequestBody Producto producto) {return productoRepository.save(producto);}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public void deleteProducto(@PathVariable Long id) { productoRepository.deleteById(id);}
}
