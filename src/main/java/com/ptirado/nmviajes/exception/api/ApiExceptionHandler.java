package com.ptirado.nmviajes.exception.api;

import com.ptirado.nmviajes.util.DateUtils;
import com.ptirado.nmviajes.util.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageUtils messageUtils;

    // ================================
    //   Manejo de ApiException base
    // ================================
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {

        String message = messageUtils.getMessage(ex.getMessageKey(), ex.getArgs());

        return ResponseEntity.status(ex.getStatus()).body(
                ErrorResponse.builder()
                        .timestamp(DateUtils.format(LocalDateTime.now()))
                        .status(ex.getStatus().value())
                        .error(ex.getClass().getSimpleName())
                        .message(message)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // =====================================
    //  Errores de validación @Valid (400)
    // =====================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {

        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage(),
                        (a, b) -> a // si se repite campo, mantener primero
                ));

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .timestamp(DateUtils.format(LocalDateTime.now()))
                        .status(400)
                        .error("ValidationError")
                        .message("Error de validación")
                        .errors(errors)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    // =====================================
    //   Fallback global (500)
    // =====================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex,
                                                            HttpServletRequest request) {

        return ResponseEntity.status(500).body(
                ErrorResponse.builder()
                        .timestamp(DateUtils.format(LocalDateTime.now()))
                        .status(500)
                        .error("InternalServerError")
                        .message("Error interno del servidor")
                        .path(request.getRequestURI())
                        .build()
        );
    }
}
