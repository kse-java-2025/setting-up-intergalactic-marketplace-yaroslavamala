package com.cosmocats.cosmomarket.repository;

import com.cosmocats.cosmomarket.repository.entity.CategoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<CategoryEntity, Long> {
   
}
