package com.store.product.service.impl;

import com.store.product.domain.Product;
import com.store.product.repository.ProductRepository;
import com.store.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends GenericServiceImpl<Product, Long, ProductRepository> implements ProductService {
    public ProductServiceImpl(ProductRepository repository){ super(repository); }

    public void updateProductQuantity(Long productId, int newQuantity) {
        Product product = get(productId, "Produto não encontrado com o ID: ");


        if (newQuantity >= 0) {
            if (newQuantity <= product.getQuantity()) {
                int quantitySub = product.getQuantity() - newQuantity;
                product.setQuantity(quantitySub);
                update(product);
            } else {
                throw new IllegalArgumentException("A nova quantidade não pode ser maior que a quantidade atual.");
            }
        } else {
            throw new IllegalArgumentException("A nova quantidade não pode ser menor que 0.");
        }
    }
}
