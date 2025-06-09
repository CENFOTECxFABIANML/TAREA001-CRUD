package com.project.demo.rest.product;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if(foundProduct.isPresent()) {
            product.setId(foundProduct.get().getId());
            productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    product, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
//    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
//        return productRepository.findById(id)
//                .map(existingProduct -> {
//                    existingProduct.setName(product.getName());
//                    existingProduct.setDescription(product.getDescription());
//                    existingProduct.setPrice(product.getPrice());
//                    existingProduct.setStock(product.getStock());
//                    return productRepository.save(existingProduct);
//                })
//                .orElseGet(() -> {
//                    product.setId(id);
//                    return productRepository.save(product);
//                });
//    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody Product product, HttpServletRequest request) {
        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse("Product created successfully",
                savedProduct, HttpStatus.CREATED, request);
    }

//    @PostMapping
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
//    public Product addProduct(@RequestBody Product product) {return productRepository.save(product);}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if(foundProduct.isPresent()) {
            productRepository.deleteById(foundProduct.get().getId());
            return new GlobalResponseHandler().handleResponse("Product deleted successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
//    public void deleteProduct(@PathVariable Long id) { productRepository.deleteById(id);}
}
