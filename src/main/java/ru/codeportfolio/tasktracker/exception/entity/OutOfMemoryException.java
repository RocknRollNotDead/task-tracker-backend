package ru.codeportfolio.tasktracker.exception.entity;

public class OutOfMemoryException extends RuntimeException {
    public OutOfMemoryException(String message) {
        super(message);
    }

    public OutOfMemoryException() {
        super();
    }
}
