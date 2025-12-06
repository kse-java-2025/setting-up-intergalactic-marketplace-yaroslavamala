package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.dto.cart.CartDto;
import com.cosmocats.cosmomarket.dto.cart.CartItemCreateDto;
import com.cosmocats.cosmomarket.dto.cart.CartItemUpdateDto;
import com.cosmocats.cosmomarket.service.CartServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartServiceInterface service;

    public CartController(CartServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartDto createCart() {
        return service.createNewCart();
    }

    @GetMapping
    public List<CartDto> getAllCarts() {
        return service.getAllCart();
    }

    @GetMapping("/{id}")
    public CartDto getCartById(@PathVariable UUID id) {
        return service.getCartById(id);
    }

    @PostMapping("/{cartId}/items")
    public CartDto addProductToCart(@PathVariable UUID cartId, @Valid @RequestBody CartItemCreateDto dto) {
        return service.addProductToCart(cartId, dto.getProductId(), dto.getQuantity());
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public CartDto updateCartItemQuantity(@PathVariable UUID cartId, @PathVariable UUID itemId, @Valid @RequestBody CartItemUpdateDto dto) {
        return service.updateCartItemQuantity(cartId, itemId, dto.getQuantity());
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public CartDto removeItemFromCart(@PathVariable UUID cartId, @PathVariable UUID itemId) {
        return service.removeItemFromCart(cartId, itemId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@PathVariable UUID id) {
        service.deleteCart(id);
    }
}
