package com.dariaob.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    // ид
    private Long id;
    // Артикул
    private String name;
    // кол-во
    private Integer amount;
    // сумма
    private Integer price;
    // дата заказа
    private LocalDateTime purchaseDate;

    public Integer getPrice() {
        return price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAmount() {
        return amount;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
}
