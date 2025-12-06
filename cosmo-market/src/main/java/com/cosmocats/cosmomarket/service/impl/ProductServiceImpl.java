package com.cosmocats.cosmomarket.service.impl;

import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import com.cosmocats.cosmomarket.exception.CategoryNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.CategoryRepository;
import com.cosmocats.cosmomarket.repository.ProductRepository;
import com.cosmocats.cosmomarket.repository.entity.CategoryEntity;
import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import com.cosmocats.cosmomarket.service.ProductServiceInterface;
import com.cosmocats.cosmomarket.service.mapper.ProductMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductServiceInterface {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductReturnDto createNewProduct(ProductCreateDto dto) {
        long categoryId = dto.getCategoryId();
        CategoryEntity category = categoryRepo.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        ProductEntity toSaveProduct = productMapper.buildProduct(dto);
        toSaveProduct.setCategory(category);
        ProductEntity savedProduct = productRepo.save(toSaveProduct);
        return productMapper.buildProductReturnDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductReturnDto> getAllProducts() {
        return productMapper.buildListProductReturnDto(productRepo.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReturnDto getProductById(UUID id) {
        ProductEntity product = productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.buildProductReturnDto(product);
    }

    @Override
    @Transactional
    public ProductReturnDto updateProduct(UUID id, ProductUpdateDto dto) {
        ProductEntity existingProduct = productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        if (dto.getCategoryId() != null) {
            long categoryId = dto.getCategoryId();
            CategoryEntity category = categoryRepo.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
            existingProduct.setCategory(category);
        }

        productMapper.updateEntityFromDto(dto, existingProduct);
        ProductEntity savedProduct = productRepo.save(existingProduct);
        
        return productMapper.buildProductReturnDto(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        productRepo.deleteById(id);
    }
}
