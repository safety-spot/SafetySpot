package spot.safety.ssbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spot.safety.ssbackend.dto.ErrorResponse;
import spot.safety.ssbackend.exception.DuplicateTagException;

import java.time.Instant;

/**
 * Catches all Errors thrown by Rest Controllers
 * Error Types:
 * - handleNotFound = 404
 * - accessDenied = 403
 * - argumentNotValid = 400
 * - errorOccurred = 500
 * Error Formatting:
 * <ErrorCode | Error Message | Timestamp>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound (EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(spot.safety.ssbackend.exception.UsernameAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> usernameTaken(spot.safety.ssbackend.exception.UsernameAlreadyTakenException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(DuplicateTagException.class)
    public ResponseEntity<ErrorResponse> duplicateTag(DuplicateTagException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> argumentNotValid (MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> messageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorOccurred(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(500, ex.getMessage(), Instant.now()));
    }
}
