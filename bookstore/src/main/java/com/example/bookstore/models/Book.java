package com.example.bookstore.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "price")
    private Double price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "total_sold", nullable = false)
    private Integer totalSold = 0; // Default value matching DB

    @Column(name = "last_restock_date")
    private LocalDateTime lastRestockDate;

    // Kolom ini sekarang VARCHAR(500) NOT NULL di DB.
    // Kita harus memastikan selalu ada nilai yang disetel.
    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public Book() {
        // Penting: Setel default URL untuk imageUrl karena kolom DB adalah NOT NULL.
        // Pastikan 'default_book.png' ada di src/main/resources/static/images/books/
        this.imageUrl = "/images/books/default_book.png";
    }

    public Book(String title, String author, Double price, Integer stock) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.totalSold = 0;
        this.imageUrl = "/images/books/default_book.png"; // Setel default URL
    }

    public Book(String title, String author, Double price, Integer stock, String imageUrl) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.totalSold = 0;
        // Pastikan imageUrl yang masuk tidak null/kosong. Jika ya, gunakan default.
        this.imageUrl = (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl : "/images/books/default_book.png";
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Integer totalSold) {
        this.totalSold = totalSold;
    }

    public LocalDateTime getLastRestockDate() {
        return lastRestockDate;
    }

    public void setLastRestockDate(LocalDateTime lastRestockDate) {
        this.lastRestockDate = lastRestockDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        // Selalu pastikan imageUrl tidak null atau kosong jika disetel secara eksternal
        this.imageUrl = (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl : "/images/books/default_book.png";
    }
}