package com.example.catalogservice.catalog.service;

import com.example.catalogservice.catalog.domain.CatalogEntity;

import java.util.List;

public interface CatalogService {

    List<CatalogEntity> getAllCatalogs();
}
