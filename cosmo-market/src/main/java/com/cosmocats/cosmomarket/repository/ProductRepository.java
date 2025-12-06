package com.cosmocats.cosmomarket.repository;

import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends NaturalIdRepository<ProductEntity, UUID> {

    List<ProductEntity> findByCategoryId(Long categoryId);

    List<ProductEntity> findByName(String name);
}
