package com.bookstore.api.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "bookType")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "images")
    private String images;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "author")
    private String author;

    @Column(name = "company")
    private String company;

    @Column(name = "old_price")
    private Double oldPrice;

    @ManyToOne
    @JoinColumn(name = "book_type_id")
    private BookType bookType;
}
