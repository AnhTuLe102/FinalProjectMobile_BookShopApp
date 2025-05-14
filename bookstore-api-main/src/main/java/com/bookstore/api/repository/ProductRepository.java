package com.bookstore.api.repository;

import com.bookstore.api.entity.product.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByBookTypeId(int bookTypeId);
}
