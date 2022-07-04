package com.example.test_server.services;

import com.example.test_server.models.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public List<Product> getLists() {

        List<Product> rsList = new ArrayList<>();

        int i = 0;
        for (; i < 5; i++) {

            rsList.add(new Product(
                    (long) i,
                    "PHONE " + i,
                    "BRAND " + i,
                    "img" + i + ".png",
                    10000,
                    Arrays.asList("BLACK", "WHITE", "RED"),
                    new BigDecimal("100000")
            ));

        }
        return rsList;
    }
}
