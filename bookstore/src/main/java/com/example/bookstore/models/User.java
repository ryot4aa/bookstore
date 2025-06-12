    package com.example.bookstore.models;

    import jakarta.persistence.Entity;
    import jakarta.persistence.Id;
    import jakarta.persistence.GeneratedValue; // Diaktifkan kembali
    import jakarta.persistence.GenerationType; // Diaktifkan kembali
    import jakarta.persistence.Column;
    import jakarta.persistence.Enumerated;
    import jakarta.persistence.EnumType;

    @Entity // Menandakan bahwa kelas ini adalah entitas JPA
    public class User {

        @Id // Menandakan bahwa user_id adalah primary key
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Gunakan ini jika user_id auto-increment di DB
        private Long userId;

        @Column(nullable = false, unique = true) // username tidak boleh null dan harus unik
        private String username;

        @Column(nullable = false) // password tidak boleh null
        private String password;

        @Enumerated(EnumType.STRING) // Menyimpan enum sebagai String di database
        @Column(nullable = false) // role tidak boleh null
        private Role role;

        // Enum untuk role
        public enum Role {
            admin, user
        }

        // Konstruktor default
        public User() {
        }

        // Konstruktor dengan parameter (userId dihapus karena akan di-generate otomatis)
        public User(String username, String password, Role role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        // Getter dan Setter
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
        }
    }
