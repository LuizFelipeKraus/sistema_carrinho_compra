package com.store.product.controller;

import com.store.product.domain.Product;
import com.store.product.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
public class ProductController extends GenericController<Product>{
    public ProductController(ProductService service){ super(service); }
}