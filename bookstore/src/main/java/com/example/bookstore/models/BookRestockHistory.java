package com.example.bookstore.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Menandakan bahwa kelas ini adalah entitas JPA yang memetakan ke tabel database
@Table(name = "book_restock_history") // Nama tabel di database
public class BookRestockHistory {

    @Id // Menandakan primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Menggunakan auto-increment
    @Column(name = "restock_id") // Nama kolom di database
    private Long restockId;

    @ManyToOne(fetch = FetchType.LAZY) // Many-to-one relationship with Book
    @JoinColumn(name = "book_id", nullable = false) // Foreign key ke tabel book
    private Book book;

    @Column(name = "restock_quantity", nullable = false) // Jumlah buku yang di-restock
    private Integer restockQuantity;

    @Column(name = "restock_date", nullable = false) // Tanggal dan waktu restock
    private LocalDateTime restockDate;

    @ManyToOne(fetch = FetchType.LAZY) // Many-to-one relationship with User (the admin who restocked)
    @JoinColumn(name = "restocked_by_user_id") // Foreign key ke tabel user (bisa nullable jika tidak selalu dicatat)
    private User restockedByUser; // Admin yang melakukan restock

    // Constructors
    public BookRestockHistory() {
    }

    public BookRestockHistory(Book book, Integer restockQuantity, LocalDateTime restockDate, User restockedByUser) {
        this.book = book;
        this.restockQuantity = restockQuantity;
        this.restockDate = restockDate;
        this.restockedByUser = restockedByUser;
    }

    // Getters and Setters
    public Long getRestockId() {
        return restockId;
    }

    public void setRestockId(Long restockId) {
        this.restockId = restockId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getRestockQuantity() {
        return restockQuantity;
    }

    public void setRestockQuantity(Integer restockQuantity) {
        this.restockQuantity = restockQuantity;
    }

    public LocalDateTime getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(LocalDateTime restockDate) {
        this.restockDate = restockDate;
    }

    public User getRestockedByUser() {
        return restockedByUser;
    }

    public void setRestockedByUser(User restockedByUser) {
        this.restockedByUser = restockedByUser;
    }

    @Override
    public String toString() {
        return "BookRestockHistory{" +
               "restockId=" + restockId +
               ", book=" + (book != null ? book.getTitle() : "null") +
               ", restockQuantity=" + restockQuantity +
               ", restockDate=" + restockDate +
               ", restockedByUser=" + (restockedByUser != null ? restockedByUser.getUsername() : "null") +
               '}';
    }
}
