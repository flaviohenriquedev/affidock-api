package com.affidock.api.modules.products.controller;

import com.affidock.api.common.base.BaseController;
import com.affidock.api.modules.products.dto.ProductEnrichRequest;
import com.affidock.api.modules.products.dto.ProductEnrichResponse;
import com.affidock.api.modules.products.dto.ProductRequest;
import com.affidock.api.modules.products.dto.ProductResponse;
import com.affidock.api.modules.products.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController extends BaseController<ProductRequest, ProductResponse> {
    private final ProductService productService;

    public ProductController(ProductService service) {
        super(service);
        this.productService = service;
    }

    @PostMapping("/enrich-from-url")
    public ResponseEntity<ProductEnrichResponse> enrichFromUrl(@Valid @RequestBody ProductEnrichRequest request) {
        return ResponseEntity.ok(productService.enrichFromAffiliateUrl(request.affiliateUrl()));
    }
}
