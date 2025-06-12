package com.example.bookstore.repositories; // Perubahan package

import com.example.bookstore.models.User; // Perubahan import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Menandakan bahwa ini adalah komponen Spring Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Menambahkan method kustom untuk mencari user berdasarkan username
    Optional<User> findByUsername(String username);
}
