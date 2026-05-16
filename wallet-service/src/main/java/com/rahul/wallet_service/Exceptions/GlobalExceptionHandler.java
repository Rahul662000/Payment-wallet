package com.rahul.wallet_service.Exceptions;

import com.rahul.wallet_service.Dtos.ApiResopnse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ================= Runtime Exception =================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResopnse<String>> handleRuntimeResponse(RuntimeException ex){
        log.error("Runtime exception", ex);
        ApiResopnse<String> response = ApiResopnse.<String>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .timeStamo(LocalDateTime.now())
                        .build();
        return ResponseEntity.badRequest().body(response);
    }

    // ================= Validation Exception =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String , String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );
        return ResponseEntity.badRequest()
                .body(errors);
    }

    // ================= Generic Exception =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResopnse<String>> handleException(Exception ex){
        log.error("Unhandled exception", ex);
        ApiResopnse<String> resopnse = ApiResopnse.<String>builder()
                .success(false)
                .message("Internal server error")
                .data(null)
                .timeStamo(LocalDateTime.now())
                .build();
        return ResponseEntity.internalServerError()
                .body(resopnse);
    }


}
