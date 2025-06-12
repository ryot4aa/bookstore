package com.example.bookstore.services;

import com.example.bookstore.models.User;
import com.example.bookstore.models.User.Role;
import com.example.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service // Menandakan bahwa ini adalah komponen Service Spring
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // Inisialisasi Logger

    @Autowired // Melakukan injeksi dependensi UserRepository
    private UserRepository userRepository;

    // Mendapatkan semua user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Mendapatkan user berdasarkan ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Menyimpan atau mengupdate user
    public User saveUser(User user) {
        logger.info("Attempting to save user: {}", user.getUsername()); // Log sebelum save
        try {
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully: {}", savedUser.getUsername()); // Log setelah save
            return savedUser;
        } catch (Exception e) {
            logger.error("Error saving user {}: {}", user.getUsername(), e.getMessage(), e); // Log error
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e); // Re-throw untuk ditangani di controller
        }
    }

    // Menghapus user berdasarkan ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Validasi login
    public Optional<User> validateLogin(String username, String password) {
        logger.info("Attempting login for username: '{}'", username);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // --- Tambahan Logging Penting di Sini ---
            logger.info("User found in DB. Retrieved username: '{}', password: '{}', role: '{}'",
                        user.getUsername(), user.getPassword(), user.getRole());
            // --- Akhir Tambahan Logging ---

            logger.info("Password from form: '{}'", password);
            logger.info("Password from DB:   '{}'", user.getPassword());

            if (user.getPassword().equals(password)) {
                logger.info("Password match. Login successful.");
                return Optional.of(user);
            } else {
                logger.warn("Password mismatch for user: {}", username);
            }
        } else {
            logger.warn("User '{}' not found in DB.", username);
        }
        return Optional.empty();
    }

    // Menambahkan user baru dengan role default 'user'
    public User registerUser(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(Role.user); // Default role untuk registrasi
        return saveUser(newUser); // Memanggil saveUser yang sudah ada logging-nya
    }

    // Method baru: Mencari user berdasarkan username
    public Optional<User> findByUsername(String username) {
        logger.info("Searching for user by username: '{}'", username); // Tambahan logging
        Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isPresent()) {
            logger.info("User '{}' found in repository.", username);
        } else {
            logger.info("User '{}' NOT found in repository.", username);
        }
        return foundUser;
    }
}
