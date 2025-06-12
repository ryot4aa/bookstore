package com.example.bookstore.services;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.Transaction;
import com.example.bookstore.models.User;
import com.example.bookstore.repositories.BookRepository;
import com.example.bookstore.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Menandakan bahwa ini adalah komponen Service Spring
public class TransactionService {

    @Autowired // Melakukan injeksi dependensi TransactionRepository
    private TransactionRepository transactionRepository;

    @Autowired // Melakukan injeksi dependensi BookRepository
    private BookRepository bookRepository; // Perlu untuk mengurangi stok

    // Mendapatkan riwayat transaksi untuk user tertentu
    public List<Transaction> getTransactionHistoryByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    // Mendapatkan transaksi berdasarkan ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Method baru: Mendapatkan semua transaksi
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Memproses pembelian buku
    @Transactional // Pastikan operasi ini bersifat transaksional
    public boolean purchaseBook(User user, Long bookId, int quantity) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (book.getStock() >= quantity) {
                // Kurangi stok buku
                book.setStock(book.getStock() - quantity);
                // Tambah total terjual
                book.setTotalSold(book.getTotalSold() + quantity);
                bookRepository.save(book);

                // Buat entitas transaksi baru
                Transaction transaction = new Transaction();
                transaction.setUser(user);
                transaction.setBook(book);
                transaction.setQuantity(quantity);
                transaction.setTotalPrice(book.getPrice() * quantity);
                transaction.setTransactionDate(LocalDateTime.now());

                // Simpan transaksi
                transactionRepository.save(transaction);
                return true; // Pembelian berhasil
            }
        }
        return false; // Buku tidak ditemukan atau stok tidak cukup
    }

    // Method baru: Menghapus transaksi berdasarkan ID
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
