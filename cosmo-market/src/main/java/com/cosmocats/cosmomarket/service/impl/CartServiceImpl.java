package com.cosmocats.cosmomarket.service.impl;

import com.cosmocats.cosmomarket.dto.cart.CartDto;
import com.cosmocats.cosmomarket.exception.CartItemNotFoundException;
import com.cosmocats.cosmomarket.exception.CartNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.CartRepository;
import com.cosmocats.cosmomarket.repository.ProductRepository;
import com.cosmocats.cosmomarket.repository.entity.CartEntity;
import com.cosmocats.cosmomarket.repository.entity.CartItemEntity;
import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import com.cosmocats.cosmomarket.service.CartServiceInterface;
import com.cosmocats.cosmomarket.service.mapper.CartMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartServiceInterface {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartDto createNewCart() {
        CartEntity toSaveCart = CartEntity.builder()
                .createdAt(OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();
        
        CartEntity savedCart = cartRepo.save(toSaveCart);
        return cartMapper.buildCartDto(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartDto> getAllCart() {
        return cartMapper.buildListCartDto(cartRepo.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartById(UUID id) {
        CartEntity cart = cartRepo.findById(id).orElseThrow(() -> new CartNotFoundException(id));
        return cartMapper.buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto addProductToCart(UUID cartId, UUID productId, Integer quantity) {
        CartEntity cart = cartRepo.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        ProductEntity product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        
        CartItemEntity existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }
        
        CartEntity savedCart = cartRepo.save(cart);
        return cartMapper.buildCartDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto updateCartItemQuantity(UUID cartId, UUID itemId, Integer quantity) {
        CartEntity cart = cartRepo.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        
        CartItemEntity item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemId, cartId));
        
        item.setQuantity(quantity);
        
        CartEntity savedCart = cartRepo.save(cart);
        return cartMapper.buildCartDto(savedCart);
    }

    @Override
    @Transactional
    public CartDto removeItemFromCart(UUID cartId, UUID itemId) {
        CartEntity cart = cartRepo.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        
        CartEntity savedCart = cartRepo.save(cart);
        return cartMapper.buildCartDto(savedCart);
    }

    @Override
    @Transactional
    public void deleteCart(UUID id) {
        cartRepo.deleteById(id);
    }
}
