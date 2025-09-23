package ir.lms.dto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDTO {
    private String massage;
    private boolean success;
}
