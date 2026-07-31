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

    @Column(unique = true, nullable = false, length = 45)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Role role;

    public User(String login, String password, Role role, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User() {

    }
}
