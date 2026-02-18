package com.example.catalogservice.catalog.service;

import com.example.catalogservice.catalog.domain.CatalogEntity;
import com.example.catalogservice.catalog.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카탈로그 상품 재고를 조회 및 변경하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CatalogService {

    private final CatalogRepository catalogRepository;

    /**
     * 전체 카탈로그 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    /**
     * 주문 요청 기반으로 재고를 차감한다.
     *
     * @param productId 상품 ID
     * @param qty       차감 수량
     * @return 차감 후 잔여 재고
     */
    public int decreaseStock(String productId, int qty) {
        validateQty(productId, qty);

        CatalogEntity entity = catalogRepository.findByProductId(productId);

        if (entity == null) {
            throw new IllegalArgumentException("not found productId: " + productId);
        }

        if (entity.getStock() < qty) {
            throw new IllegalStateException("not enough stock for productId: " + productId);
        }

        entity.setStock(entity.getStock() - qty);
        catalogRepository.save(entity);

        return entity.getStock();
    }

    /**
     * 주문 보상 처리에서 차감된 재고를 복원한다.
     *
     * @param productId 상품 ID
     * @param qty       복원 수량
     * @return 복원 후 잔여 재고
     */
    public int restoreStock(String productId, int qty) {
        validateQty(productId, qty);

        CatalogEntity entity = catalogRepository.findByProductId(productId);

        if (entity == null) {
            throw new IllegalArgumentException("not found productId: " + productId);
        }

        entity.setStock(entity.getStock() + qty);
        catalogRepository.save(entity);

        return entity.getStock();
    }

    /**
     * 요청 수량이 음수/0인지 검증한다.
     */
    private void validateQty(String productId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("qty should be positive for productId: " + productId);
        }
    }
}
