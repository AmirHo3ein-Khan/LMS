package ir.lms.exception;

public class AdminAlreadyExistException extends RuntimeException {
    public AdminAlreadyExistException(String message) {
        super(message);
    }
}
