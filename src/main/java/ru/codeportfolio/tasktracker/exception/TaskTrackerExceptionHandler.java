package ru.codeportfolio.tasktracker.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.codeportfolio.tasktracker.exception.entity.AlreadyExistException;
import ru.codeportfolio.tasktracker.exception.entity.NotFoundException;
import ru.codeportfolio.tasktracker.exception.entity.ValidationException;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class TaskTrackerExceptionHandler {


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(ValidationException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(MethodArgumentTypeMismatchException e) {

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Not right %s! %s = %s".formatted(
                        e.getName(),
                        e.getName(),
                        e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown"
                ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(HandlerMethodValidationException e) {

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Validation error! %s".formatted(e.getDetailMessageArguments()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation error");

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(NotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleGeneric(AlreadyExistException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
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
