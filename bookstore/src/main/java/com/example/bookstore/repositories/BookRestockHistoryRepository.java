package com.example.bookstore.repositories;

import com.example.bookstore.models.BookRestockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Menandakan bahwa ini adalah komponen Repository Spring Data JPA
public interface BookRestockHistoryRepository extends JpaRepository<BookRestockHistory, Long> {
    // Anda bisa menambahkan method kustom di sini jika diperlukan,
    // misalnya mencari riwayat restock berdasarkan buku atau user.
    List<BookRestockHistory> findByBook_BookId(Long bookId);
}
