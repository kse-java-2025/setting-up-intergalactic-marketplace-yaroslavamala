package com.cosmocats.cosmomarket.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_item")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    UUID id;

    @Column(nullable = false)
    Integer quantity;

    @Column(name = "item_price", nullable = false)
    BigDecimal itemPrice;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    ProductEntity product;
}
