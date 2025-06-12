package com.example.bookstore.services;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.BookRestockHistory;
import com.example.bookstore.models.User;
import com.example.bookstore.repositories.BookRepository;
import com.example.bookstore.repositories.BookRestockHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookRestockService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRestockHistoryRepository restockHistoryRepository;

    @Transactional
    public boolean restockBook(Long bookId, Integer quantityToAdd, User restockedByUser) {
        if (quantityToAdd <= 0) {
            return false;
        }

        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            // Tambahkan stok buku
            book.setStock(book.getStock() + quantityToAdd);
            // NEW: Update last_restock_date di entitas Book
            book.setLastRestockDate(LocalDateTime.now());
            bookRepository.save(book);

            // Catat riwayat restock
            BookRestockHistory restockHistory = new BookRestockHistory();
            restockHistory.setBook(book);
            restockHistory.setRestockQuantity(quantityToAdd);
            restockHistory.setRestockDate(LocalDateTime.now());
            restockHistory.setRestockedByUser(restockedByUser);
            restockHistoryRepository.save(restockHistory);

            return true;
        }
        return false;
    }

    public List<BookRestockHistory> getRestockHistoryForBook(Long bookId) {
        return restockHistoryRepository.findByBook_BookId(bookId);
    }

    public List<BookRestockHistory> getAllRestockHistory() {
        return restockHistoryRepository.findAll();
    }
}
