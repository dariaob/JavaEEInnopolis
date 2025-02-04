package com.dariaob.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final DistributionSummary orderAmountSummary;
    private Counter orderCounter;
    private double totalAmount = 0;
    private int totalOrders = 0;

    public ProductService(MeterRegistry registry) {
        // Инициализация метрик
        this.orderCounter = registry.counter("order.count");
        this.orderAmountSummary = registry.summary("order.amount.summary");
    }

    private double calculateAverage(ProductService productService) {
        return totalOrders == 0 ? 0 : totalAmount / totalOrders;
    }

    // Метод для увеличения счетчика заказов
    public void incrementOrderCount() {
        orderCounter.increment();
        totalOrders++;
    }

    // Метод для добавления суммы заказа
    public void addOrderAmount(double amount) {
        totalAmount += amount;
        orderAmountSummary.record(amount);
    }

    // Метод для вычисления среднего чека
    public double calculateAverageOrderAmount() {
        return totalOrders == 0 ? 0 : totalAmount / totalOrders;
    }

    // Метод для получения общего количества заказов
    public int getTotalOrders() {
        return totalOrders;
    }

    // Метод для получения общей суммы заказов
    public double getTotalAmount() {
        return totalAmount;
    }
}
