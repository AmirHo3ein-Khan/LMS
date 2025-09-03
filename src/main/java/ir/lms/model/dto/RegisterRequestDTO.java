package ir.lms.model.dto;

public record RegisterRequestDTO(
        String nationalCode ,
        String firstName,
        String lastName,
        String username ,
        String password ,
        String role ,
        String email
) {
}
