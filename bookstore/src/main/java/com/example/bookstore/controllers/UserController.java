package com.example.bookstore.controllers;

import com.example.bookstore.models.User;
import com.example.bookstore.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showUserProfile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Anda perlu login untuk melihat profil.");
            return "redirect:/login";
        }

        // Ambil data user terbaru dari DB berdasarkan username yang ada di sesi
        Optional<User> userFromDb = userService.findByUsername(loggedInUser.getUsername());
        if (userFromDb.isPresent()) {
            User currentUser = userFromDb.get();
            model.addAttribute("loggedInUser", currentUser);
            session.setAttribute("loggedInUser", currentUser); // Perbarui sesi dengan data terbaru
        } else {
            // Jika user tidak ditemukan di DB (misalnya sudah dihapus), paksa logout
            redirectAttributes.addFlashAttribute("error", "Data profil tidak ditemukan. Silakan login kembali.");
            session.invalidate(); // Hancurkan sesi
            return "redirect:/login";
        }

        model.addAttribute("userRole", userRole); // Role tetap diambil dari sesi

        return "user/profile";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Anda perlu login untuk mengedit profil.");
            return "redirect:/login";
        }

        Optional<User> userFromDb = userService.findByUsername(loggedInUser.getUsername());
        if (userFromDb.isPresent()) {
            model.addAttribute("userToEdit", userFromDb.get());
        } else {
            redirectAttributes.addFlashAttribute("error", "Data profil tidak ditemukan.");
            session.invalidate(); // Hancurkan sesi
            return "redirect:/login";
        }

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", (String) session.getAttribute("userRole"));
        return "user/edit_profile";
    }

    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute("userToEdit") User userToEdit, // Binding untuk userId dan username
            @RequestParam(value = "currentPassword", required = false) String currentPassword, // Password saat ini (opsional)
            @RequestParam(value = "newPassword", required = false) String newPassword,     // Password baru (opsional)
            @RequestParam(value = "confirmNewPassword", required = false) String confirmNewPassword, // Konfirmasi password baru (opsional)
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Sesi tidak valid atau mencoba mengedit pengguna lain.");
            return "redirect:/login";
        }

        try {
            // Ambil user dari DB menggunakan username yang ada di sesi (lebih aman)
            Optional<User> existingUserOptional = userService.findByUsername(loggedInUser.getUsername());
            if (existingUserOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Pengguna tidak ditemukan.");
                return "redirect:/profile";
            }
            User existingUser = existingUserOptional.get();

            // 1. Update Username (jika username di form berbeda dengan yang di DB)
            if (!existingUser.getUsername().equals(userToEdit.getUsername())) {
                // Periksa apakah username baru sudah ada
                if (userService.findByUsername(userToEdit.getUsername()).isPresent() &&
                    !userService.findByUsername(userToEdit.getUsername()).get().getUserId().equals(existingUser.getUserId())) {
                    redirectAttributes.addFlashAttribute("error", "Username '" + userToEdit.getUsername() + "' sudah digunakan oleh pengguna lain.");
                    return "redirect:/profile/edit";
                }
                existingUser.setUsername(userToEdit.getUsername());
            }

            // 2. Update Password (jika ada input password baru)
            // Cek apakah ada satupun field password yang diisi
            boolean passwordFieldsFilled = (currentPassword != null && !currentPassword.isEmpty()) ||
                                           (newPassword != null && !newPassword.isEmpty()) ||
                                           (confirmNewPassword != null && !confirmNewPassword.isEmpty());

            if (passwordFieldsFilled) {
                // Validasi semua field password harus diisi jika ingin mengubah password
                if (currentPassword == null || currentPassword.isEmpty() ||
                    newPassword == null || newPassword.isEmpty() ||
                    confirmNewPassword == null || confirmNewPassword.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Untuk mengubah password, semua field password harus diisi.");
                    return "redirect:/profile/edit";
                }

                // Validasi password saat ini
                if (!existingUser.getPassword().equals(currentPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Password saat ini salah.");
                    return "redirect:/profile/edit";
                }

                // Validasi password baru dan konfirmasi
                if (!newPassword.equals(confirmNewPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Password baru dan konfirmasi tidak cocok.");
                    return "redirect:/profile/edit";
                }
                
                // Cegah password baru sama dengan password lama
                if (newPassword.equals(currentPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Password baru tidak boleh sama dengan password saat ini.");
                    return "redirect:/profile/edit";
                }

                // Update password
                existingUser.setPassword(newPassword); // INGAT: Dalam produksi, ini HARUS di-hash (misal: pakai BCryptPasswordEncoder)
            }

            // Simpan semua perubahan (username dan/atau password)
            userService.saveUser(existingUser);

            // Perbarui sesi dengan data user terbaru
            session.setAttribute("loggedInUser", existingUser);
            
            redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui!");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui profil: " + e.getMessage());
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/profile/edit"; // Tetap di halaman edit jika ada error
        }
    }

    // Method showChangePasswordForm dan changePassword sudah dihapus atau tidak digunakan lagi
    // karena fungsionalitasnya digabungkan ke /profile/edit
}