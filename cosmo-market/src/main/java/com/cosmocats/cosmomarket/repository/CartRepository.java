package com.cosmocats.cosmomarket.repository;

import com.cosmocats.cosmomarket.repository.entity.CartEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import com.cosmocats.cosmomarket.repository.entity.OrderEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends NaturalIdRepository<CartEntity, UUID> {

    List<OrderEntity> findByCreatedAtBetween(OffsetDateTime createdAt, OffsetDateTime createdAt2);

    List<CartEntity> findByCreatedAtBefore(OffsetDateTime createdAt);
}
