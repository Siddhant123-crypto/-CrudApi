package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Cart;
import com.Siddhant.UserApp.Entity.CartItem;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.CartItemRepository;
import com.Siddhant.UserApp.Repository.CartRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.CartService;
import com.Siddhant.UserApp.dto.CartItemResponse;
import com.Siddhant.UserApp.dto.CartQuantityRequest;
import com.Siddhant.UserApp.dto.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private User getLoggedInUser() {Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }@Override
    @Transactional
    public CartResponse addToCart(UUID productId, CartQuantityRequest request) {
        User customer = getLoggedInUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        validateProduct(product);
        validateQuantity(request);

        BigDecimal quantity = request.getQuantity();

        if (quantity.compareTo(BigDecimal.valueOf(product.getQuantity())) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity is not available");
        }

        Cart cart = cartRepository.findByCustomerUserId(customer.getUserId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            newCart.setTotalAmount(BigDecimal.ZERO);
            return cartRepository.save(newCart);
        });

        CartItem cartItem = cartItemRepository
                .findByCartCartIdAndProductProductId(cart.getCartId(), productId)
                .orElse(null);

        BigDecimal newQuantity = quantity;

        if (cartItem != null) {
            newQuantity = cartItem.getQuantity().add(quantity);

            if (newQuantity.compareTo(BigDecimal.valueOf(product.getQuantity())) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total quantity exceeds available stock");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cart.getCartItems().add(cartItem);
        }

        BigDecimal price = BigDecimal.valueOf(product.getPrice());

        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(price);
        cartItem.setSubtotal(price.multiply(newQuantity));

        cartItemRepository.save(cartItem);

        updateCartTotal(cart);

        return convertToResponse(cart);
    }@Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        User customer = getLoggedInUser();
        Cart cart = cartRepository.findByCustomerUserId(customer.getUserId()).orElse(null);
        if (cart == null) {
            CartResponse emptyResponse = new CartResponse();
            emptyResponse.setTotalAmount(BigDecimal.ZERO);
            emptyResponse.setTotalItems(0);
            emptyResponse.setItems(new java.util.ArrayList<>());
            return emptyResponse;
        }
        return convertToResponse(cart);
    }@Override
    @Transactional
    public CartResponse updateQuantity(UUID productId, CartQuantityRequest request) {
        User customer = getLoggedInUser();validateQuantity(request);
        Cart cart = cartRepository.findByCustomerUserId(customer.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem cartItem = cartItemRepository.findByCartCartIdAndProductProductId(cart.getCartId(), productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not in your cart"));
        Product product = cartItem.getProduct();
        validateProduct(product);
        BigDecimal quantity = request.getQuantity();
        if (quantity.compareTo(BigDecimal.valueOf(product.getQuantity())) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity is not available");
        }BigDecimal price = BigDecimal.valueOf(product.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(price);
        cartItem.setSubtotal(price.multiply(quantity));
        cartItemRepository.save(cartItem);
        updateCartTotal(cart);
        return convertToResponse(cart);
    }@Override
    @Transactional
    public void removeFromCart(UUID productId) {
        User customer = getLoggedInUser();
        Cart cart = cartRepository.findByCustomerUserId(customer.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem cartItem = cartItemRepository.findByCartCartIdAndProductProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not in your cart"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        updateCartTotal(cart);
    }@Override
    @Transactional
    public void clearCart() {
        User customer = getLoggedInUser();
        Cart cart = cartRepository.findByCustomerUserId(customer.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }private void validateProduct(Product product) {
        if (Boolean.FALSE.equals(product.getIsActive()) || Boolean.TRUE.equals(product.getIsDelete())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This product is not available");
        }
    }private void validateQuantity(CartQuantityRequest request) {
        if (request == null || request.getQuantity() == null ||
                request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }
    }private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);cart.setTotalAmount(total);cartRepository.save(cart);
    }private CartResponse convertToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream().map(this::convertToItemResponse).collect(Collectors.toList());CartResponse response = new CartResponse();response.setCartId(cart.getCartId());response.setTotalAmount(cart.getTotalAmount());response.setTotalItems(items.size());response.setItems(items);response.setCreatedAt(cart.getCreatedAt());response.setUpdatedAt(cart.getUpdatedAt());
        return response;
    }private CartItemResponse convertToItemResponse(CartItem item) {
        Product product = item.getProduct();CartItemResponse response = new CartItemResponse();response.setCartItemId(item.getCartItemId());response.setProductId(product.getProductId());response.setProductName(product.getProductName());response.setImageUrl(product.getProductPhoto());response.setQuantity(item.getQuantity());response.setUnit(product.getUnit() != null ? product.getUnit().toString() : null);response.setPrice(item.getPrice());response.setSubtotal(item.getSubtotal());
        return response;
    }
}
