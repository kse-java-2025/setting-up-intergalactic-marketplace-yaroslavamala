package com.cosmocats.cosmomarket.repository.interfaces;

import com.cosmocats.cosmomarket.domain.product.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryInterface {
    Product saveProduct(Product product);
    Optional<Product> findById(UUID id);
    List<Product> getAllProducts();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
