package ir.lms.exception;

public class ExamExpiredException extends RuntimeException {
    public ExamExpiredException(String message) {
        super(message);
    }
}
