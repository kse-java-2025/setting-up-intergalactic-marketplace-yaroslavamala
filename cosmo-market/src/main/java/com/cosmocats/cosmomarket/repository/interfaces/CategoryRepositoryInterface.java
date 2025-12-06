package com.cosmocats.cosmomarket.repository.interfaces;

import com.cosmocats.cosmomarket.domain.category.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryInterface {
    Category saveCategory(Category category);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> getAllCategories();
    boolean existsById(Long id);
    void deleteById(Long id);
}
