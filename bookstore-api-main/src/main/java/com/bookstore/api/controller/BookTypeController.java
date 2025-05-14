package com.bookstore.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.api.entity.product.BookType;
import com.bookstore.api.repository.BookTypeRepository;

@RestController
@RequestMapping("/api/v1/book-type")
public class BookTypeController {
    @Autowired
    private BookTypeRepository bookTypeRepository;

    @GetMapping
    public List<BookType> getAllBookTypes() {
        return bookTypeRepository.findAll();
    }

    @GetMapping("/{id}")
    public BookType getBookTypeById(int id) {
        return bookTypeRepository.findById(id).orElse(null);
    }
}
