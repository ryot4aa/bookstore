package com.example.bookstore.repositories; // Perubahan package

import com.example.bookstore.models.Transaction; // Perubahan import
import com.example.bookstore.models.User;       // Perubahan import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Menandakan bahwa ini adalah komponen Spring Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Menambahkan method kustom untuk mencari transaksi berdasarkan user
    List<Transaction> findByUser(User user);
}
