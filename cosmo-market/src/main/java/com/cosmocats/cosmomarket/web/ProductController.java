package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import com.cosmocats.cosmomarket.service.ProductServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductServiceInterface service;

    public ProductController(ProductServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductReturnDto createProduct(@Valid @RequestBody ProductCreateDto dto) {
        return service.createNewProduct(dto);
    }

    @GetMapping
    public List<ProductReturnDto> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductReturnDto getProductById(@PathVariable UUID id) {
        return service.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductReturnDto updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductUpdateDto dto) {
        return service.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        service.deleteProduct(id);
    }
}
