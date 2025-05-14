package com.bookstore.api.service;

import java.util.List;

import com.bookstore.api.entity.product.BookType;

public interface BookTypeService {
    List<BookType> findAll();
}
