package ir.lms.util.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
        @NotBlank(message = "First name cannot be empty")
        @Size(max = 50, message = "First name should not exceed 50 characters")
        private String firstName;

        @NotBlank(message = "Last name cannot be empty")
        @Size(max = 50, message = "Last name should not exceed 50 characters")
        private String lastName;

        @NotBlank(message = "National code is required")
        @Pattern(regexp = "\\d{10}", message = "National code must be exactly 10 digits")
        private String nationalCode;

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^(\\+98|0)?9\\d{9}$", message = "Phone number must be a valid Iranian mobile number")
        private String phoneNumber;

        @NotBlank(message = "Major name cannot be empty")
        private String majorName;
}
