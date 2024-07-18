package com.example.catalogservice.catalog.service;

import com.example.catalogservice.catalog.domain.CatalogEntity;
import com.example.catalogservice.catalog.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public List<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}
