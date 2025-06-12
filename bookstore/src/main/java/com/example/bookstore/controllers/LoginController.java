package com.example.bookstore.controllers;

import com.example.bookstore.models.User;
import com.example.bookstore.models.User.Role; // Pastikan ini diimpor untuk Role
import com.example.bookstore.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Import untuk ModelAttribute
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller // Menandakan bahwa kelas ini adalah komponen Controller Spring
public class LoginController {

    @Autowired // Melakukan injeksi dependensi UserService
    private UserService userService;

    // Method untuk menampilkan halaman login (GET request ke /login)
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Menambahkan objek User kosong ke model untuk form binding di Thymeleaf
        model.addAttribute("user", new User());
        return "auth/login"; // Mengembalikan nama template Thymeleaf: src/main/resources/templates/auth/login.html
    }

    // Method untuk memproses submit form login (POST request ke /login)
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, // Mengambil nilai dari input 'username' di form
                               @RequestParam String password, // Mengambil nilai dari input 'password' di form
                               HttpSession session, // Untuk menyimpan informasi sesi user
                               RedirectAttributes redirectAttributes) { // Untuk mengirim pesan flash ke halaman lain

        Optional<User> userOptional = userService.validateLogin(username, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Login berhasil
            session.setAttribute("loggedInUser", user); // Simpan objek user di sesi
            session.setAttribute("userRole", user.getRole().name()); // Simpan role user di sesi

            // Redirect berdasarkan role
            if (user.getRole() == User.Role.admin) {
                // Jika Anda belum membuat dashboard admin, bisa diarahkan ke /books dulu
                // return "redirect:/admin/dashboard";
                return "redirect:/books";
            } else {
                return "redirect:/"; // Redirect ke halaman daftar buku untuk user biasa
            }
        } else {
            // Login gagal
            redirectAttributes.addFlashAttribute("error", "Username atau password salah!");
            return "redirect:/login"; // Kembali ke halaman login dengan pesan error
        }
    }

    // Method untuk logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Menghapus semua atribut sesi
        return "redirect:/login?logout"; // Redirect ke halaman login dengan parameter logout
    }

    // --- Fungsionalitas Registrasi ---

    // Method untuk menampilkan halaman registrasi (GET request ke /register)
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); // Menambahkan objek User kosong untuk form binding
        return "auth/register"; // Mengembalikan nama template Thymeleaf: src/main/resources/templates/auth/register.html
    }

    // Method untuk memproses submit form registrasi (POST request ke /register)
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, // Mengikat data form ke objek User
                                      RedirectAttributes redirectAttributes) {
        // Cek apakah username sudah ada
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username sudah terdaftar. Silakan gunakan username lain.");
            return "redirect:/register";
        }

        // Set role default sebagai 'user'
        user.setRole(Role.user);
        // Di sini Anda bisa menambahkan logika untuk mengenkripsi password sebelum menyimpan
        // user.setPassword(passwordEncoder.encode(user.getPassword())); // Contoh jika pakai Spring Security
        userService.saveUser(user); // Simpan user baru ke database

        redirectAttributes.addFlashAttribute("success", "Registrasi berhasil! Silakan login.");
        return "redirect:/login"; // Redirect ke halaman login setelah registrasi berhasil
    }
}
