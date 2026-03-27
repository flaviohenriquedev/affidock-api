package com.affidock.api.modules.products.controller;

import com.affidock.api.common.base.BaseController;
import com.affidock.api.modules.products.dto.ProductRequest;
import com.affidock.api.modules.products.dto.ProductResponse;
import com.affidock.api.modules.products.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController extends BaseController<ProductRequest, ProductResponse> {
    public ProductController(ProductService service) {
        super(service);
    }
}
