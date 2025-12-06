package com.cosmocats.cosmomarket.repository;

import com.cosmocats.cosmomarket.repository.entity.OrderEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends NaturalIdRepository<OrderEntity, UUID> {

    List<OrderEntity> findByCreatedAtBetween(OffsetDateTime createdAt, OffsetDateTime createdAt2);

    List<OrderEntity> findByCreatedAtBefore(OffsetDateTime createdAt);
}
