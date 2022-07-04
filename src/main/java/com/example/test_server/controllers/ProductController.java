package com.example.test_server.controllers;

import com.example.test_server.models.Product;
import com.example.test_server.services.ProductService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/product")
@CrossOrigin(value = "*")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/lists")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Product> retrieveListProduct(){
        return productService.getLists();
    }
}
