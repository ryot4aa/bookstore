package com.example.bookstore.models; // Perubahan package

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne; // Untuk relasi Many-to-One
import jakarta.persistence.JoinColumn; // Untuk menentukan kolom join
import java.time.LocalDateTime; // Untuk tipe data TIMESTAMP

@Entity // Menandakan bahwa kelas ini adalah entitas JPA
public class Transaction {

    @Id // Menandakan bahwa transaction_id adalah primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // transaction_id auto-increment
    private Long transactionId;

    @ManyToOne // Relasi Many-to-One dengan User
    @JoinColumn(name = "user_id") // Kolom foreign key di tabel transactions
    private User user;

    @ManyToOne // Relasi Many-to-One dengan Book
    @JoinColumn(name = "book_id", nullable = false) // Kolom foreign key di tabel transactions, tidak boleh null
    private Book book;

    @Column(nullable = false) // quantity tidak boleh null
    private Integer quantity;

    private Double totalPrice;

    @Column(name = "transaction_date") // Nama kolom di database
    private LocalDateTime transactionDate; // Menggunakan LocalDateTime untuk TIMESTAMP

    // Konstruktor default
    public Transaction() {
        this.transactionDate = LocalDateTime.now(); // Set tanggal transaksi saat objek dibuat
    }

    // Konstruktor dengan parameter
    public Transaction(User user, Book book, Integer quantity, Double totalPrice) {
        this.user = user;
        this.book = book;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.transactionDate = LocalDateTime.now();
    }

    // Getter dan Setter
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "transactionId=" + transactionId +
               ", user=" + (user != null ? user.getUsername() : "null") +
               ", book=" + (book != null ? book.getTitle() : "null") +
               ", quantity=" + quantity +
               ", totalPrice=" + totalPrice +
               ", transactionDate=" + transactionDate +
               '}';
    }
}
