package org.dariaob;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    private Integer id;
    // описание
    private String description;
    // стоимость
    private Double price;
    // количество
    private Integer count;
}
