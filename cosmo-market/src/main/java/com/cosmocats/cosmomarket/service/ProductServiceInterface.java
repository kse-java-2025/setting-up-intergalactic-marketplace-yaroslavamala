package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import java.util.List;
import java.util.UUID;

public interface ProductServiceInterface {
    ProductReturnDto createNewProduct(ProductCreateDto dto);
    List<ProductReturnDto> getAllProducts();
    ProductReturnDto getProductById(UUID id);
    ProductReturnDto updateProduct(UUID id, ProductUpdateDto dto);
    void deleteProduct(UUID id);
}
