package com.example.catalogservice.catalog.controller;

import com.example.catalogservice.catalog.domain.CatalogEntity;
import com.example.catalogservice.catalog.service.CatalogService;
import com.example.catalogservice.catalog.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog-service/catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    private final ModelMapper modelMapper;

    /**
     * 상품 전체 목록 조회
     * @return 상품 목록
     */
    @GetMapping
    public ResponseEntity<List<ResponseCatalog>> getCatalogs() {

        List<CatalogEntity> catalogList = catalogService.getAllCatalogs();
        List<ResponseCatalog> resCatalogs = new ArrayList<>();

        catalogList.forEach(catalog -> resCatalogs.add(modelMapper.map(catalog, ResponseCatalog.class)));

        return ResponseEntity.status(HttpStatus.OK).body(resCatalogs);
    }

}
