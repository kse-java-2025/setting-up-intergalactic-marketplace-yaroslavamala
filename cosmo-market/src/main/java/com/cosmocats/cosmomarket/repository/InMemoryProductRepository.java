package com.cosmocats.cosmomarket.repository;

import com.cosmocats.cosmomarket.domain.product.Product;
import com.cosmocats.cosmomarket.domain.category.Category;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProductRepository implements ProductRepositoryInterface {
    private final Map<UUID, Product> productStorage = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        productStorage.put(id1, Product.builder()
                .id(id1)
                .name("Cosmo Coffee Example")
                .description("Space beans")
                .category(Category.FOOD)
                .availableQuantity(42)
                .price(new BigDecimal("9.99"))
                .build());
        productStorage.put(id2, Product.builder()
                .id(id2)
                .name("Galaxy Coat Example")
                .description("Beautiful comfy coat")
                .category(Category.CLOTHES)
                .availableQuantity(10)
                .price(new BigDecimal("19.95"))
                .build());
    }

    public Product saveProduct(Product product) {
        Product newProduct = product.getId() == null
                ? product.toBuilder().id(UUID.randomUUID()).build()
                : product;
        productStorage.put(newProduct.getId(), newProduct);
        return newProduct;
    }

    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(productStorage.get(id));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productStorage.values());
    }

    public boolean existsById(UUID id) {
        return productStorage.containsKey(id);
    }

    public void deleteById(UUID id) {
        productStorage.remove(id);
    }
}
