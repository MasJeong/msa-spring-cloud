package com.example.catalogservice.temp.controller;

import com.example.catalogservice.temp.domain.CartItem;
import com.example.catalogservice.temp.service.CartService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

    /** Cart Service */
    private final CartService cartService;

    /**
     * 장바구니에 상품 추가
     *
     * @param userId 사용자 ID
     * @param item 추가할 장바구니 아이템
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addCartItem(@PathVariable String userId, @RequestBody CartItem item) {
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException();
        }

        cartService.addItem(userId, item);

        return ResponseEntity.ok().build();
    }

    /**
     * 장바구니 조회
     *
     * @param userId 사용자 ID
     * @return 장바구니 내용 (Key: 상품ID, Value: CartItem 객체)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, CartItem>> getCart(@PathVariable String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException();
        }

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    /**
     * 장바구니 상품 수량 업데이트
     *
     * @param userId 사용자 ID
     * @param productId 수량을 변경할 상품 ID
     * @param item 변경할 수량
     */
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> updateQuantity(@PathVariable String userId,
                                               @PathVariable String productId,
                                               @RequestBody CartItem item) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(productId)) {
            throw new BadRequestException();
        }

        cartService.updateQuantity(userId, productId, item.getQuantity());

        return ResponseEntity.ok().build();
    }
}