package com.bookstore.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.api.entity.product.BookType;

public interface BookTypeRepository extends JpaRepository<BookType, Integer> {

}
