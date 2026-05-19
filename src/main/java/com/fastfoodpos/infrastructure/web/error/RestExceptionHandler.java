package com.fastfoodpos.infrastructure.web.error;

import com.fastfoodpos.domain.exception.DuplicateMenuItemException;
import com.fastfoodpos.domain.exception.MenuItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("Solicitud invalida", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("Solicitud invalida", List.of("El cuerpo JSON no es valido")));
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMenuItemNotFound(MenuItemNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("Recurso no encontrado", List.of(exception.getMessage())));
    }

    @ExceptionHandler(DuplicateMenuItemException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateMenuItem(DuplicateMenuItemException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("Conflicto de negocio", List.of(exception.getMessage())));
    }
}
