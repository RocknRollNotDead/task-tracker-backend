package ru.codeportfolio.tasktracker.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private Role role;

    public User(String username, String password, Role role, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User() {

    }
}
