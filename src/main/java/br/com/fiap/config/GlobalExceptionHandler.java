package br.com.fiap.config;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

// Nota: esta classe foi desativada como @ControllerAdvice para evitar conflito com
// br.com.fiap.exception.GlobalExceptionHandler (que é o handler REST principal).
// Em vez de remover o código, mantive a implementação para referência futura.
// Para reativar, restaure as anotações @Component e @ControllerAdvice abaixo.

// @Component("configGlobalExceptionHandler")
// @ControllerAdvice
@Deprecated
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // NOTE: methods left intentionally unchanged but they will not be invoked while
    // this class is not registered as a ControllerAdvice bean.

    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = messageSource.getMessage(error, LocaleContextHolder.getLocale());
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        String msg = messageSource.getMessage("error.generic", null, LocaleContextHolder.getLocale());
        return new ResponseEntity<>(Collections.singletonMap("error", msg), HttpStatus.CONFLICT);
    }

    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        String msg = messageSource.getMessage("auth.unauthorized", null, LocaleContextHolder.getLocale());
        return new ResponseEntity<>(Collections.singletonMap("error", msg), HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        String msg = messageSource.getMessage("error.generic", null, LocaleContextHolder.getLocale());
        return new ResponseEntity<>(Collections.singletonMap("error", msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
