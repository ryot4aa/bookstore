package com.example.bookstore.controllers;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.User;
import com.example.bookstore.services.BookRestockService;
import com.example.bookstore.services.BookService;
import com.example.bookstore.services.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import ini
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils; // Import ini
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile; // Import ini
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException; // Import ini
import java.nio.file.Files; // Import ini
import java.nio.file.Path; // Import ini
import java.nio.file.Paths; // Import ini
import java.nio.file.StandardCopyOption; // Import ini
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BookRestockService bookRestockService;

    // Injeksi direktori upload dari application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    // --- MODIFIKASI: Method home() untuk mengirim rekomendasi buku ---
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", userRole);

        // Ambil beberapa buku untuk rekomendasi (misalnya 5 buku pertama)
        List<Book> allBooks = bookService.getAllBooks();
        // Anda bisa menambahkan logika yang lebih canggih di sini untuk rekomendasi
        // Misalnya:
        // .sorted(Comparator.comparing(Book::getTotalSold).reversed()) // Buku terlaris
        // .sorted(Comparator.comparing(Book::getLastRestockDate).reversed()) // Buku terbaru
        List<Book> recommendedBooks = allBooks.stream()
                                            .limit(5) // Mengambil 5 buku pertama dari daftar semua buku
                                            .collect(Collectors.toList());
        
        model.addAttribute("recommendedBooks", recommendedBooks); // Tambahkan ke model

        return "home"; // Mengarahkan ke templates/home.html
    }
    // --- AKHIR MODIFIKASI METHOD home() ---

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", userRole);
        return "about"; // Mengarahkan ke templates/about.html
    }

    @GetMapping("/books")
    public String listBooks(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", session.getAttribute("userRole"));

        return "books/list";
    }

    // Method to display the form for adding a new book (GET request to /admin/books/add)
    @GetMapping("/admin/books/add")
    public String showAddBookForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses ke halaman ini.");
            return "redirect:/login";
        }

        model.addAttribute("book", new Book());
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", userRole);
        return "books/add";
    }

    // Method to process the submission of the add book form (POST request to /admin/books/add)
    @PostMapping("/admin/books/add")
    public String addBook(@ModelAttribute Book book,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk melakukan tindakan ini.");
            return "redirect:/login";
        }

        try {
            System.out.println("--- Inside addBook method ---");
            System.out.println("Is imageFile null? " + (imageFile == null));
            if (imageFile != null) {
                System.out.println("Is imageFile empty? " + imageFile.isEmpty());
                System.out.println("Original filename: " + imageFile.getOriginalFilename());
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                // Pastikan folder upload ada
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
                }

                // Simpan file ke sistem file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Set URL gambar di objek buku (relatif ke static folder Spring Boot)
                book.setImageUrl("/images/books/" + fileName);
                System.out.println("Image URL set on book object: " + book.getImageUrl());
            } else {
                System.out.println("No image file uploaded or file is empty for new book.");
                // Opsional: set default image jika tidak ada upload
                // book.setImageUrl("/images/books/default_book.png");
            }

            // Set initial restock date when adding a new book
            book.setLastRestockDate(LocalDateTime.now());
            
            System.out.println("Saving book with final imageUrl: " + book.getImageUrl());
            bookService.saveBook(book); // Simpan buku ke database
            
            redirectAttributes.addFlashAttribute("success", "Buku berhasil ditambahkan!");
            return "redirect:/books";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengunggah gambar: " + e.getMessage());
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace(); // Cetak stack trace untuk detail error
            return "redirect:/admin/books/add"; // Kembali ke form jika ada error upload
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan buku: " + e.getMessage());
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace(); // Cetak stack trace untuk detail error
            return "redirect:/admin/books/add"; // Kembali ke form jika ada error lain
        }
    }

    // Fungsionalitas Edit Buku (Admin Only)
    @GetMapping("/admin/books/edit/{id}")
    public String showEditBookForm(@PathVariable("id") Long bookId,
                                Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk mengedit buku.");
            return "redirect:/login";
        }

        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (bookOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Buku tidak ditemukan.");
            return "redirect:/books";
        }

        model.addAttribute("book", bookOptional.get());
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", userRole);
        return "books/edit";
    }

    @PostMapping("/admin/books/edit")
    public String editBook(@ModelAttribute Book book,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk menyimpan perubahan buku.");
            return "redirect:/login";
        }

        if (book.getBookId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID buku tidak valid untuk update.");
            return "redirect:/books";
        }

        try {
            System.out.println("--- Inside editBook method ---");
            System.out.println("Book ID being edited: " + book.getBookId());
            System.out.println("Is imageFile null? " + (imageFile == null));
            if (imageFile != null) {
                System.out.println("Is imageFile empty? " + imageFile.isEmpty());
                System.out.println("Original filename: " + imageFile.getOriginalFilename());
            }

            Optional<Book> existingBookOptional = bookService.getBookById(book.getBookId());
            if (existingBookOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Buku tidak ditemukan untuk diperbarui.");
                return "redirect:/books";
            }
            Book existingBook = existingBookOptional.get();
            System.out.println("Current imageUrl from DB (before potential update): " + existingBook.getImageUrl());

            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setPrice(book.getPrice());
            existingBook.setStock(book.getStock());

            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    System.out.println("Created upload directory during edit: " + uploadPath.toAbsolutePath());
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                existingBook.setImageUrl("/images/books/" + fileName);
                System.out.println("New Image URL set on existing book object: " + existingBook.getImageUrl());
            } else {
                System.out.println("No new image file uploaded during edit. Retaining existing imageUrl: " + existingBook.getImageUrl());
            }

            System.out.println("Saving updated book with final imageUrl: " + existingBook.getImageUrl());
            bookService.saveBook(existingBook);
            
            redirectAttributes.addFlashAttribute("success", "Buku berhasil diperbarui!");
            return "redirect:/books";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengunggah gambar: " + e.getMessage());
            System.err.println("Error uploading image during edit: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/books/edit/" + book.getBookId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui buku: " + e.getMessage());
            System.err.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/books/edit/" + book.getBookId();
        }
    }

    // Fungsionalitas Hapus Buku (Admin Only)
    @GetMapping("/admin/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long bookId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk menghapus buku.");
            return "redirect:/login";
        }

        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (bookOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Buku tidak ditemukan.");
            return "redirect:/books";
        }

        try {
            if (bookOptional.get().getImageUrl() != null && !bookOptional.get().getImageUrl().isEmpty()) {
                String imageUrl = bookOptional.get().getImageUrl();
                String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                Path imagePath = Paths.get(uploadDir, fileName);

                System.out.println("Attempting to delete image file: " + imagePath.toAbsolutePath());
                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                    System.out.println("Image file deleted successfully: " + imagePath);
                } else {
                    System.out.println("Image file not found at path: " + imagePath + ". Skipping deletion.");
                }
            } else {
                System.out.println("No imageUrl found for bookId: " + bookId + ". Skipping image file deletion.");
            }

            bookService.deleteBook(bookId);
            redirectAttributes.addFlashAttribute("success", "Buku berhasil dihapus!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus gambar terkait: " + e.getMessage());
            System.err.println("Error deleting image file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus buku: " + e.getMessage());
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/books";
    }

    // Fungsionalitas Pembelian Buku Massal
    @PostMapping("/books/prepare-purchase")
    public String preparePurchase(@RequestParam(value = "selectedBookIds", required = false) List<Long> selectedBookIds,
                                  Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (selectedBookIds == null || selectedBookIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tidak ada buku yang dipilih untuk dibeli.");
            return "redirect:/books";
        }

        List<Book> selectedBooks = bookService.getBooksByIds(selectedBookIds);

        selectedBooks = selectedBooks.stream()
                .filter(book -> book.getStock() > 0)
                .collect(Collectors.toList());

        if (selectedBooks.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Buku yang dipilih tidak tersedia atau stok habis.");
            return "redirect:/books";
        }


        model.addAttribute("selectedBooks", selectedBooks);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", session.getAttribute("userRole"));
        return "books/checkout";
    }

    @PostMapping("/books/complete-purchase")
    public String completePurchase(@RequestParam Map<String, String> quantities,
                                   HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Map<Long, Integer> purchaseItems = quantities.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("quantity_"))
                .collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey().replace("quantity_", "")),
                        entry -> Integer.parseInt(entry.getValue())
                ));

        if (purchaseItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tidak ada buku atau jumlah yang valid untuk dibeli.");
            return "redirect:/books";
        }

        boolean allPurchasesSuccessful = true;
        StringBuilder errorMessage = new StringBuilder();

        for (Map.Entry<Long, Integer> entry : purchaseItems.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity <= 0) {
                errorMessage.append("Jumlah untuk buku ID ").append(bookId).append(" harus lebih dari 0. ");
                allPurchasesSuccessful = false;
                continue;
            }

            Optional<Book> bookOptional = bookService.getBookById(bookId);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                if (book.getStock() < quantity) {
                    errorMessage.append("Stok buku '").append(book.getTitle()).append("' tidak cukup (tersedia: ").append(book.getStock()).append(", diminta: ").append(quantity).append("). ");
                    allPurchasesSuccessful = false;
                } else {
                    boolean success = transactionService.purchaseBook(loggedInUser, bookId, quantity);
                    if (!success) {
                        allPurchasesSuccessful = false;
                        errorMessage.append("Gagal memproses pembelian buku '").append(book.getTitle()).append("'. ");
                    }
                }
            } else {
                allPurchasesSuccessful = false;
                errorMessage.append("Buku dengan ID ").append(bookId).append(" tidak ditemukan. ");
            }
        }

        if (allPurchasesSuccessful) {
            redirectAttributes.addFlashAttribute("success", "Pembelian berhasil diselesaikan!");
            return "redirect:/transactions/history";
        } else {
            redirectAttributes.addFlashAttribute("error", "Beberapa pembelian gagal: " + errorMessage.toString().trim());
            return "redirect:/books";
        }
    }

    // Fungsionalitas Restock Buku (Admin Only)

    // Menampilkan form restock buku
    @GetMapping("/admin/books/restock/{id}")
    public String showRestockForm(@PathVariable("id") Long bookId,
                                Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk melakukan restock buku.");
            return "redirect:/login";
        }

        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (bookOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Buku tidak ditemukan.");
            return "redirect:/books";
        }

        model.addAttribute("book", bookOptional.get());
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("userRole", userRole);
        return "books/restock";
    }

    // Memproses restock buku
    @PostMapping("/admin/books/restock")
    public String processRestock(@RequestParam("bookId") Long bookId,
                                @RequestParam("quantity") Integer quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String userRole = (String) session.getAttribute("userRole");

        if (loggedInUser == null || !User.Role.admin.name().equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Anda tidak memiliki akses untuk memproses restock buku.");
            return "redirect:/login";
        }

        if (quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Jumlah restock harus lebih dari 0.");
            return "redirect:/admin/books/restock/" + bookId;
        }

        boolean success = bookRestockService.restockBook(bookId, quantity, loggedInUser);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Stok buku berhasil di-restock!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Gagal melakukan restock. Buku tidak ditemukan.");
        }
        return "redirect:/books";
    }
}