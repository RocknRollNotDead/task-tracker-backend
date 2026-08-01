package ru.codeportfolio.tasktracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.codeportfolio.tasktracker.exception.entity.AlreadyExistException;

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
    @ManyToOne
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

    public void setStatus(){
        if (status == Status.DONE){
//            throw new AlreadyExistException("This status %s already exist".formatted(Status.DONE));
            // я ввобще хотел сделать так ^, но увидел, что в тз необходим обратный разворот
            status = Status.IN_PROGRESS;
            timestamp = null;
        } else {
            status = Status.DONE;
            timestamp = Timestamp.from(Instant.now());
        }
    }

}
