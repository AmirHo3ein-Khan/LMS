package ir.lms.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}