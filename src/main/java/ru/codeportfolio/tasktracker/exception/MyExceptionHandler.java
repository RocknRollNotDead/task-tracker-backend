package ru.codeportfolio.tasktracker.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.codeportfolio.tasktracker.exception.entity.AlreadyExistException;
import ru.codeportfolio.tasktracker.exception.entity.NotFoundException;
import ru.codeportfolio.tasktracker.exception.entity.OutOfMemoryException;
import ru.codeportfolio.tasktracker.exception.entity.ValidationException;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class MyExceptionHandler {


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(ValidationException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(BadCredentialsException e) {

        return buildResponse(HttpStatus.UNAUTHORIZED, "Not right login or password! ");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(MethodArgumentNotValidException e) {

        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthenticationException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "User not authorized!");//e.getMessage()

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(AccessDeniedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied!!!");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(NotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(AlreadyExistException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(OutOfMemoryException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(OutOfMemoryException e) {
        return buildResponse(HttpStatus.INSUFFICIENT_STORAGE, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(RuntimeException e) {
        log.error(e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error!");
    }


    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        Map<String, String> body = Map.of(
                "message", message);
        return ResponseEntity.status(status).body(body);
    }

}
