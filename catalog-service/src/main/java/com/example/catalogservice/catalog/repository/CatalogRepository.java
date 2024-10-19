package com.example.catalogservice.catalog.repository;

import com.example.catalogservice.catalog.domain.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    CatalogEntity findByProductId(String productId);
}
