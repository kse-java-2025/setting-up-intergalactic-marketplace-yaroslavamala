package com.cosmocats.cosmomarket.service.mapper;

import com.cosmocats.cosmomarket.dto.category.CategoryCreateDto;
import com.cosmocats.cosmomarket.dto.category.CategoryReturnDto;
import com.cosmocats.cosmomarket.repository.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryReturnDto buildCategoryReturnDto(CategoryEntity category);

    List<CategoryReturnDto> buildListCategoryReturnDto(List<CategoryEntity> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    CategoryEntity buildCategory(CategoryCreateDto dto);
}
