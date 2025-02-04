package com.dariaob.actuator;

import com.dariaob.service.ProductService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomActuator {
    private final ProductService productService;

    @Autowired
    public CustomActuator(ProductService productService, MeterRegistry registry) {
        this.productService = productService;

        // Регистрируем кастомные метрики
        Gauge.builder("orders.average.amount", productService, ProductService::calculateAverageOrderAmount)
                .description("Средний чек")
                .register(registry);

        Gauge.builder("orders.total.count", productService, ProductService::getTotalOrders)
                .description("Общее количество заказов")
                .register(registry);

        Gauge.builder("orders.total.amount", productService, ProductService::getTotalAmount)
                .description("Общая сумма заказов")
                .register(registry);
    }

}

