package com.cosmocats.cosmomarket.domain.order;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class Order {

    @Builder.Default
    UUID id = UUID.randomUUID();

    @Builder.Default
    OffsetDateTime createdAt = OffsetDateTime.now();

    @Singular("item")
    List<OrderItem> items;

    public BigDecimal totalOrderPrice() {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal itemTotal = item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        return total;
    }
}
