package com.example.bookstore.controllers;

import com.example.bookstore.models.Transaction;
import com.example.bookstore.models.User;
import com.example.bookstore.services.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import PathVariable
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional; // Import Optional

@Controller // Menandakan bahwa kelas ini adalah komponen Controller Spring
public class AdminTransactionController {

    @Autowired // Melakukan injeksi dependensi TransactionService
    private TransactionService transactionService;

    // Method untuk menampilkan semua riwayat transaksi (hanya untuk admin)
    @GetMapping("/admin/transactions/all")
    public String showAllTransactions(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        // Periksa apakah user sudah login dan memiliki role admin
        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke halaman ini.");
            return "redirect:/login"; // Redirect ke halaman login jika bukan admin
        }

        List<Transaction> transactions = transactionService.getAllTransactions(); // Memanggil service untuk mendapatkan semua transaksi
        model.addAttribute("transactions", transactions); // Tambahkan daftar transaksi ke model
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", userRole);

        return "transactions/all"; // Mengembalikan template: src/main/resources/templates/transactions/all.html
    }

    // Method untuk menghapus transaksi berdasarkan ID (hanya untuk admin)
    @GetMapping("/admin/transactions/delete/{id}")
    public String deleteTransaction(@PathVariable("id") Long transactionId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        // Periksa apakah user sudah login dan memiliki role admin
        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk menghapus transaksi.");
            return "redirect:/login"; // Redirect ke halaman login jika bukan admin
        }

        Optional<Transaction> transactionOptional = transactionService.getTransactionById(transactionId);
        if (transactionOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Transaksi tidak ditemukan.");
            return "redirect:/admin/transactions/all";
        }

        try {
            transactionService.deleteTransaction(transactionId); // Memanggil service untuk menghapus transaksi
            redirectAttributes.addFlashAttribute("success", "Transaksi berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus transaksi: " + e.getMessage());
            System.err.println("Error deleting transaction: " + e.getMessage()); // Log error
        }

        return "redirect:/admin/transactions/all"; // Kembali ke daftar semua transaksi
    }
}
