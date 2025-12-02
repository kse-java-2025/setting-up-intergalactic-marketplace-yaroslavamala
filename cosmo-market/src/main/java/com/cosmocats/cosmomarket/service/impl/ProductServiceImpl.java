package com.cosmocats.cosmomarket.service.impl;

import com.cosmocats.cosmomarket.domain.product.Product;
import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.ProductRepositoryInterface;
import com.cosmocats.cosmomarket.service.ProductServiceInterface;
import com.cosmocats.cosmomarket.service.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductServiceInterface {

    private final ProductRepositoryInterface repo;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepositoryInterface repo, ProductMapper mapper) {
        this.repo = repo;
        this.productMapper = mapper;
    }

    @Override
    public ProductReturnDto createNewProduct(ProductCreateDto dto) {
        Product toSaveProduct = productMapper.buildProduct(dto);
        Product savedProduct = repo.saveProduct(toSaveProduct);
        return productMapper.buildProductReturnDto(savedProduct);
    }

    @Override
    public List<ProductReturnDto> getAllProducts() {
        return productMapper.buildListProductReturnDto(repo.getAllProducts());
    }

    @Override
    public ProductReturnDto getProductById(UUID id) {
        Product product = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.buildProductReturnDto(product);
    }

    @Override
    public ProductReturnDto updateProduct(UUID id, ProductUpdateDto dto) {
        Product existingProduct = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        Product updatedProduct = productMapper.applyUpdate(existingProduct, dto);
        Product savedProduct = repo.saveProduct(updatedProduct);

        return productMapper.buildProductReturnDto(savedProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        repo.deleteById(id);
    }
}
