package com.store.product.service.impl;

import com.store.product.domain.Product;
import com.store.product.repository.ProductRepository;
import com.store.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends GenericServiceImpl<Product, Long, ProductRepository> implements ProductService {
    public ProductServiceImpl(ProductRepository repository){ super(repository); }
}
