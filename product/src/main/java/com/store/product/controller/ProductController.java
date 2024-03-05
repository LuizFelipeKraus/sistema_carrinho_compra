package com.store.product.controller;

import com.store.product.domain.Product;
import com.store.product.service.impl.ProductServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
public class ProductController extends GenericController<Product>{
    private final ProductServiceImpl productService;

    public ProductController(ProductServiceImpl service){
        super(service);
        this.productService = service;
    }

    @PutMapping("/{id}/updateQuantity")
    public ResponseEntity<Void> updateProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
        productService.updateProductQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }
}