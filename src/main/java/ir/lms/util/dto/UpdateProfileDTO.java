package ir.lms.util.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 50, message = "Last name should not exceed 50 characters")
    private String lastName;

    @Pattern(
            regexp = "^(\\+98|0)?9\\d{9}$",
            message = "Phone number must be a valid Iranian mobile number"
    )
    private String phoneNumber;
}
