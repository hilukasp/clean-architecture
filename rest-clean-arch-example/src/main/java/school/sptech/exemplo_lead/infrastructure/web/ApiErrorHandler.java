package school.sptech.exemplo_lead.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.sptech.exemplo_lead.domain.exception.InvalidParamException;
import school.sptech.exemplo_lead.domain.exception.LeadNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ApiError> handleInvalidParams(
            InvalidParamException exception,
            HttpServletRequest request
    ) {
        var error = new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(error.status).body(error);
    }

    @ExceptionHandler(LeadNotFoundException.class)
    public ResponseEntity<ApiError> handleLeadNotFound(
            LeadNotFoundException exception,
            HttpServletRequest request
    ) {
        var error = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(error.status).body(error);
    }

    public record ApiError(
            Instant timestamp,
            int status,
            String message,
            String path
    ) {
    }
}
