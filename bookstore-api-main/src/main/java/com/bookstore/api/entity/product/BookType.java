package com.bookstore.api.entity.product;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "type_name")
    private String typeName;

    @JsonIgnore
    @OneToMany(mappedBy = "bookType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
}
