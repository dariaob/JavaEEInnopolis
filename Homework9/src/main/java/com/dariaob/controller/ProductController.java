package com.dariaob.controller;

import com.dariaob.dto.ProductDto;
import com.dariaob.entity.ProductEntity;
import com.dariaob.repositry.ProductRepository;
import com.dariaob.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody ProductEntity order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getPrice() <= 0 || order.getAmount() <= 0) {
            throw new IllegalArgumentException("Количество или сумма должны быть больше 0");
        }
        order.setPurchaseDate(LocalDateTime.now());
        productRepository.save(order);

        // Обновляем метрики
        productService.incrementOrderCount();
        productService.addOrderAmount(order.getAmount());

        return ResponseEntity.ok("Order created");
}
}
