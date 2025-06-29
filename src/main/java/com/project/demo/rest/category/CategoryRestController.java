package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categories retrieved successfully",
                categoriesPage.getContent(), HttpStatus.OK, meta);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if (foundCategory.isPresent()) {
            category.setId(foundCategory.get().getId());
            categoryRepository.save(category);
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    category, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
        Category savedCategory = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse("Category created successfully",
                savedCategory, HttpStatus.CREATED, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if (foundCategory.isPresent()) {
            categoryRepository.deleteById(foundCategory.get().getId());
            return new GlobalResponseHandler().handleResponse("Category deleted successfully",
                    foundCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}