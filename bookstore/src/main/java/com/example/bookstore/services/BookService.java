package com.example.bookstore.services;

import com.example.bookstore.models.Book;
import com.example.bookstore.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Menandakan bahwa ini adalah komponen Service Spring
public class BookService {

    @Autowired // Melakukan injeksi dependensi BookRepository
    private BookRepository bookRepository;

    // Mendapatkan semua buku
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Mendapatkan buku berdasarkan ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Metode baru: Mendapatkan daftar buku berdasarkan list ID
    public List<Book> getBooksByIds(List<Long> bookIds) {
        return bookRepository.findAllById(bookIds);
    }

    // Menyimpan atau mengupdate buku
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Menghapus buku berdasarkan ID
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Mengurangi stok buku setelah pembelian (method ini akan disesuaikan nanti)
    public boolean decreaseStock(Long bookId, int quantity) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (book.getStock() >= quantity) {
                book.setStock(book.getStock() - quantity);
                bookRepository.save(book);
                return true; // Stok berhasil dikurangi
            }
        }
        return false; // Buku tidak ditemukan atau stok tidak cukup
    }
}
