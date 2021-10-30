package ua.service.vehicles;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@ControllerAdvice
public class ErrorHandling {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Collection<String>> handleError(HttpMessageNotReadableException ex) {
        try {
            if (ex.getHttpInputMessage().getBody().available() == 0) {
                return ResponseEntity.badRequest().body(Collections.singletonList("request content is not supplied"));
            }
        } catch (IOException ignored) {}
        return ResponseEntity.badRequest().body(Collections.singletonList("request content unreadable"));
    }
}
