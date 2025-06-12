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

@Controller // Marks this class as a Spring Controller
public class TransactionController {

    @Autowired // Injects TransactionService dependency
    private TransactionService transactionService;

    // Method untuk menampilkan riwayat transaksi user yang login (GET request ke /transactions/history)
    @GetMapping("/transactions/history")
    public String showTransactionHistory(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect jika belum login
        }

        // Mendapatkan riwayat transaksi untuk user yang sedang login
        List<Transaction> transactions = transactionService.getTransactionHistoryByUser(loggedInUser);
        model.addAttribute("transactions", transactions); // Tambahkan daftar transaksi ke model
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", session.getAttribute("userRole"));

        return "transactions/history"; // Mengembalikan template src/main/resources/templates/transactions/history.html
    }

    // Method untuk menampilkan struk pembelian (GET request ke /transactions/receipt/{id})
    @GetMapping("/transactions/receipt/{id}")
    public String showReceipt(@PathVariable("id") Long transactionId,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect jika belum login
        }

        Optional<Transaction> transactionOptional = transactionService.getTransactionById(transactionId);

        if (transactionOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Transaksi tidak ditemukan.");
            return "redirect:/transactions/history";
        }

        Transaction transaction = transactionOptional.get();

        // Pastikan user yang login adalah pemilik transaksi, atau admin
        if (!transaction.getUser().getUserId().equals(loggedInUser.getUserId()) &&
            !User.Role.admin.name().equals(session.getAttribute("userRole"))) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke struk ini.");
            return "redirect:/transactions/history";
        }

        model.addAttribute("transaction", transaction);
        model.addAttribute("loggedInUser", loggedInUser);
        return "transactions/receipt"; // Mengembalikan template src/main/resources/templates/transactions/receipt.html
    }
}
