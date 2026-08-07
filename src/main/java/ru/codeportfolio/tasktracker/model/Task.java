package ru.codeportfolio.tasktracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Entity
@Table(name = "tasks")

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String text;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column(nullable = false)
    private Status status;

    @Column
    private Timestamp timestamp;

    public Task(String name, String text, User owner) {
        this.name = name;
        this.text = text;
        this.owner = owner;
        this.status = Status.IN_PROGRESS;
    }

    public Task() {

    }

    public void setStatus() {
        if (status == Status.DONE) {
            status = Status.IN_PROGRESS;
            timestamp = null;
        } else {
            status = Status.DONE;
            timestamp = Timestamp.from(Instant.now());
        }
    }

}
