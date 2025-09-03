package ir.lms.model.dto;

public record AuthResponseDTO (

        String accessToken,

        String refreshToken,

        String tokenType
) {
}
