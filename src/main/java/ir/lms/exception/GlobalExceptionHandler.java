package ir.lms.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateException(DuplicateException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }


    @ExceptionHandler(CourseRegisterDateException.class)
    public ResponseEntity<ExceptionResponse> handleCourseRegisterDateException(CourseRegisterDateException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(ExamNotStartedException.class)
    public ResponseEntity<ExceptionResponse> handleExamNotStartedException(ExamNotStartedException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(ExamExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleExamExpiredException(ExamExpiredException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }


    @ExceptionHandler(CourseHasNotLimitException.class)
    public ResponseEntity<ExceptionResponse> handleCourseHasNotLimitException(CourseHasNotLimitException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }
}
