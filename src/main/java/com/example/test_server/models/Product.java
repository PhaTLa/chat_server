package com.example.test_server.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    Long id;
    String name;
    String brand;
    String imgSource;
    Integer amountRemain;
    List<String >option;
    BigDecimal price;
}
