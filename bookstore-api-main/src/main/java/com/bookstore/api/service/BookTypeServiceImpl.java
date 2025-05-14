package com.bookstore.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.api.entity.product.BookType;
import com.bookstore.api.repository.BookTypeRepository;

@Service
public class BookTypeServiceImpl implements BookTypeService {

    private final BookTypeRepository bookTypeRepository;

    @Autowired
    public BookTypeServiceImpl(BookTypeRepository bookTypeRepository) {
        this.bookTypeRepository = bookTypeRepository;
    }

    @Override
    public List<BookType> findAll() {
        return bookTypeRepository.findAll();
    }

}
