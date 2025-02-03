package com.example.catalogservice.temp.service;

import com.example.catalogservice.temp.domain.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    /** redis template */
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:";

    /**
     * 장바구니에 상품 추가
     *
     * @param userId 사용자 ID
     * @param item 추가할 장바구니 아이템
     */
    public void addItem(String userId, CartItem item) {
        HashOperations<String, String, CartItem> hashOps = redisTemplate.opsForHash();

        // 사용자의 장바구니에 상품 추가 (key: 상품ID, value: CartItem 객체)
        hashOps.put(getCartKey(userId), item.getProductId(), item);
    }

    /**
     * 장바구니 조회
     *
     * @param userId 사용자 ID
     * @return 장바구니 내용 (Key: 상품ID, Value: CartItem 객체)
     */
    public Map<String, CartItem> getCart(String userId) {
        HashOperations<String, String, CartItem> hashOps = redisTemplate.opsForHash();

        // 사용자의 장바구니 전체 내용을 Map 형태로 반환
        return hashOps.entries(getCartKey(userId));
    }

    /**
     * 장바구니 상품 수량 업데이트
     *
     * @param userId 사용자 ID
     * @param productId 수량을 변경할 상품 ID
     * @param quantity 변경할 수량
     */
    public void updateQuantity(String userId, String productId, int quantity) {
        HashOperations<String, String, CartItem> hashOps = redisTemplate.opsForHash();

        // 해당 상품 정보 조회
        Optional.ofNullable(hashOps.get(getCartKey(userId), productId)).ifPresent(item -> {
            item.setQuantity(quantity);
            hashOps.put(getCartKey(userId), productId, item);
        });
    }

    /**
     * 사용자별 장바구니 키 생성
     *
     * @param userId 사용자 ID
     * @return 장바구니 키 ex) cart:9b8b3e47-7393-4883-b2a3-9a3b1bef349d
     */
    private String getCartKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }
}