package com.cosmocats.cosmomarket.config;

import com.cosmocats.cosmomarket.service.mapper.CartMapper;
import com.cosmocats.cosmomarket.service.mapper.OrderMapper;
import com.cosmocats.cosmomarket.service.mapper.ProductMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MappersTestConfiguration {

    @Bean
    public ProductMapper productMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }

    @Bean
    public CartMapper cartMapper() {
        return Mappers.getMapper(CartMapper.class);
    }

    @Bean
    public OrderMapper orderMapper() {
        return Mappers.getMapper(OrderMapper.class);
    }
}
