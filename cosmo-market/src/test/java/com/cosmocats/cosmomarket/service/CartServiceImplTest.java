package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.cart.CartDto;
import com.cosmocats.cosmomarket.exception.CartItemNotFoundException;
import com.cosmocats.cosmomarket.exception.CartNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.CartRepository;
import com.cosmocats.cosmomarket.repository.ProductRepository;
import com.cosmocats.cosmomarket.repository.entity.CartEntity;
import com.cosmocats.cosmomarket.repository.entity.CartItemEntity;
import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import com.cosmocats.cosmomarket.service.impl.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CartServiceImpl.class})
@Import(MappersTestConfiguration.class)
@DisplayName("Cart Service Tests")
public class CartServiceImplTest {

    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "Cosmic Socks";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10.5);
    private static final Integer QUANTITY = 2;
    private static final Integer UPDATED_QUANTITY = 5;
    private static final Integer ADDITIONAL_QUANTITY = 3;
    private static final String CART_NOT_FOUND_MESSAGE = "Cart not found";
    private static final String CART_ITEM_NOT_FOUND_MESSAGE = "Cart item not found";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    @MockitoBean
    private CartRepository repo;

    @MockitoBean
    private ProductRepository productRepo;

    @Captor
    private ArgumentCaptor<CartEntity> cartCaptor;

    @Autowired
    private CartServiceImpl cartService;

    private static ProductEntity buildProduct() {
        return ProductEntity.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .price(PRODUCT_PRICE)
                .build();
    }

    private static CartEntity buildCart(UUID id) {
        return CartEntity.builder()
                .id(id)
                .createdAt(OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    private static CartItemEntity buildCartItem(CartEntity cart, ProductEntity product, Integer quantity) {
        CartItemEntity item = new CartItemEntity();
        item.setId(ITEM_ID);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }

    @Test
    @DisplayName("Should create new cart successfully")
    void shouldCreateNewCartSuccessfully() {
        CartEntity savedCart = buildCart(CART_ID);

        when(repo.save(any(CartEntity.class))).thenReturn(savedCart);

        CartDto result = cartService.createNewCart();

        assertNotNull(result);
        assertEquals(CART_ID, result.getId());
        assertNotNull(result.getCreatedAt());
        assertTrue(result.getItems().isEmpty());

        verify(repo, times(1)).save(cartCaptor.capture());
        CartEntity captured = cartCaptor.getValue();
        assertNotNull(captured);
        assertNotNull(captured.getCreatedAt());
    }

    @Test
    @DisplayName("Should return all carts successfully")
    void shouldReturnAllCartsSuccessfully() {
        CartEntity cart1 = buildCart(CART_ID);
        CartEntity cart2 = buildCart(UUID.randomUUID());
        List<CartEntity> allCarts = List.of(cart1, cart2);

        when(repo.findAll()).thenReturn(allCarts);

        List<CartDto> result = cartService.getAllCart();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no carts exist")
    void shouldReturnEmptyListWhenNoCarts() {
        when(repo.findAll()).thenReturn(List.of());

        List<CartDto> result = cartService.getAllCart();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get cart by id successfully")
    void shouldGetCartByIdSuccessfully() {
        CartEntity cart = buildCart(CART_ID);

        when(repo.findById(CART_ID)).thenReturn(Optional.of(cart));

        CartDto result = cartService.getCartById(CART_ID);

        assertNotNull(result);
        assertEquals(CART_ID, result.getId());
        verify(repo, times(1)).findById(CART_ID);
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when cart not found by id")
    void shouldThrowExceptionWhenCartNotFoundById() {
        when(repo.findById(CART_ID)).thenReturn(Optional.empty());

        CartNotFoundException exception = assertThrows(CartNotFoundException.class, () -> cartService.getCartById(CART_ID));

        assertTrue(exception.getMessage().contains(CART_NOT_FOUND_MESSAGE));
        assertTrue(exception.getMessage().contains(CART_ID.toString()));
        verify(repo, times(1)).findById(CART_ID);
    }

    @Test
    @DisplayName("Should add new product to cart successfully")
    void shouldAddNewProductToCartSuccessfully() {
        CartEntity cart = buildCart(CART_ID);
        ProductEntity product = buildProduct();
        CartItemEntity item = buildCartItem(cart, product, QUANTITY);
        cart.getItems().add(item);

        when(repo.findById(CART_ID)).thenReturn(Optional.of(buildCart(CART_ID)));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(repo.save(any(CartEntity.class))).thenReturn(cart);

        CartDto result = cartService.addProductToCart(CART_ID, PRODUCT_ID, QUANTITY);

        assertNotNull(result);
        assertEquals(CART_ID, result.getId());
        assertEquals(1, result.getItems().size());

        verify(repo, times(1)).findById(CART_ID);
        verify(productRepo, times(1)).findById(PRODUCT_ID);
        verify(repo, times(1)).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should add quantity to existing product in cart")
    void shouldAddQuantityToExistingProductInCart() {
        CartEntity cart = buildCart(CART_ID);
        ProductEntity product = buildProduct();
        CartItemEntity existingItem = buildCartItem(cart, product, QUANTITY);
        cart.getItems().add(existingItem);

        CartEntity updatedCart = buildCart(CART_ID);
        CartItemEntity updatedItem = buildCartItem(updatedCart, product, QUANTITY + ADDITIONAL_QUANTITY);
        updatedCart.getItems().add(updatedItem);

        when(repo.findById(CART_ID)).thenReturn(Optional.of(cart));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(repo.save(any(CartEntity.class))).thenReturn(updatedCart);

        CartDto result = cartService.addProductToCart(CART_ID, PRODUCT_ID, ADDITIONAL_QUANTITY);

        assertNotNull(result);
        assertEquals(CART_ID, result.getId());

        verify(repo, times(1)).findById(CART_ID);
        verify(productRepo, times(1)).findById(PRODUCT_ID);
        verify(repo, times(1)).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when adding product to non-existent cart")
    void shouldThrowExceptionWhenAddingProductToNonExistentCart() {
        when(repo.findById(CART_ID)).thenReturn(Optional.empty());

        CartNotFoundException exception = assertThrows(CartNotFoundException.class, () -> cartService.addProductToCart(CART_ID, PRODUCT_ID, QUANTITY));

        assertTrue(exception.getMessage().contains(CART_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(CART_ID);
        verify(productRepo, never()).findById(any());
        verify(repo, never()).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when adding non-existent product to cart")
    void shouldThrowExceptionWhenAddingNonExistentProductToCart() {
        CartEntity cart = buildCart(CART_ID);

        when(repo.findById(CART_ID)).thenReturn(Optional.of(cart));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> cartService.addProductToCart(CART_ID, PRODUCT_ID, QUANTITY));

        assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(CART_ID);
        verify(productRepo, times(1)).findById(PRODUCT_ID);
        verify(repo, never()).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void shouldUpdateCartItemQuantitySuccessfully() {
        CartEntity cart = buildCart(CART_ID);
        ProductEntity product = buildProduct();
        CartItemEntity item = buildCartItem(cart, product, QUANTITY);
        cart.getItems().add(item);
        
        CartEntity updatedCart = buildCart(CART_ID);
        CartItemEntity updatedItem = buildCartItem(updatedCart, product, UPDATED_QUANTITY);
        updatedCart.getItems().add(updatedItem);

        when(repo.findById(CART_ID)).thenReturn(Optional.of(cart));
        when(repo.save(any(CartEntity.class))).thenReturn(updatedCart);

        CartDto result = cartService.updateCartItemQuantity(CART_ID, ITEM_ID, UPDATED_QUANTITY);

        assertNotNull(result);
        assertEquals(CART_ID, result.getId());

        verify(repo, times(1)).findById(CART_ID);
        verify(repo, times(1)).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when updating non-existent item")
    void shouldThrowExceptionWhenUpdatingNonExistentItem() {
        CartEntity cart = buildCart(CART_ID);
        UUID nonExistentItemId = UUID.randomUUID();

        when(repo.findById(CART_ID)).thenReturn(Optional.of(cart));

        CartItemNotFoundException exception = assertThrows(CartItemNotFoundException.class, () -> cartService.updateCartItemQuantity(CART_ID, nonExistentItemId, UPDATED_QUANTITY));

        assertTrue(exception.getMessage().contains(CART_ITEM_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(CART_ID);
        verify(repo, never()).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should remove item from cart successfully")
    void shouldRemoveItemFromCartSuccessfully() {
        CartEntity cart = buildCart(CART_ID);
        ProductEntity product = buildProduct();
        CartItemEntity item = buildCartItem(cart, product, QUANTITY);
        cart.getItems().add(item);

        CartEntity updatedCart = buildCart(CART_ID);

        when(repo.findById(CART_ID)).thenReturn(Optional.of(cart));
        when(repo.save(any(CartEntity.class))).thenReturn(updatedCart);

        CartDto result = cartService.removeItemFromCart(CART_ID, ITEM_ID);

        assertNotNull(result);
        assertEquals(CART_ID, result.getId());

        verify(repo, times(1)).findById(CART_ID);
        verify(repo, times(1)).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when removing item from non-existent cart")
    void shouldThrowExceptionWhenRemovingItemFromNonExistentCart() {
        when(repo.findById(CART_ID)).thenReturn(Optional.empty());

        CartNotFoundException exception = assertThrows(CartNotFoundException.class, () -> cartService.removeItemFromCart(CART_ID, ITEM_ID));

        assertTrue(exception.getMessage().contains(CART_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(CART_ID);
        verify(repo, never()).save(any(CartEntity.class));
    }

    @Test
    @DisplayName("Should delete existing cart successfully")
    void shouldDeleteExistingCartSuccessfully() {
        cartService.deleteCart(CART_ID);

        verify(repo, times(1)).deleteById(CART_ID);
    }
}
