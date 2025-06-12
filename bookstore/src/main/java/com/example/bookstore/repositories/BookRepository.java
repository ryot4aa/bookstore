package com.example.bookstore.repositories; // Perubahan package

import com.example.bookstore.models.Book; // Perubahan import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Menandakan bahwa ini adalah komponen Spring Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // JpaRepository menyediakan method CRUD dasar (save, findById, findAll, delete, dll.)
    // Kamu bisa menambahkan method kustom di sini jika diperlukan
}
