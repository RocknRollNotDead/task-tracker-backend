package ru.codeportfolio.tasktracker.exception.entity;

public class NotFoundResourceException extends RuntimeException {
    public NotFoundResourceException(String message) {
        super(message);
    }

    public NotFoundResourceException() {
    }
}
