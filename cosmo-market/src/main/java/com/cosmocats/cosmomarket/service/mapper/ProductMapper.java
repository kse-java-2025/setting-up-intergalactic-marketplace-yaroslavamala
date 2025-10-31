package com.cosmocats.cosmomarket.service.mapper;

import com.cosmocats.cosmomarket.domain.product.Product;
import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductReturnDto makeProductReturnDto(Product product);
    List<ProductReturnDto> makeListProductReturnDto(List<Product> products);

    @Mapping(target = "id", ignore = true)
    Product makeProduct(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBuilderFromDto(ProductUpdateDto dto, @MappingTarget Product.ProductBuilder builder);

    default Product applyUpdate(Product current, ProductUpdateDto dto) {
        Product.ProductBuilder builder = current.toBuilder();
        updateBuilderFromDto(dto, builder);
        return builder.build();
    }
}
