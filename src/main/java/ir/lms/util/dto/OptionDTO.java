package ir.lms.util.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO {
    @NotBlank(message = "Option text cannot be empty")
    @Size(max = 100, message = "Option text should not exceed 100 characters")
    private String optionText;

    private boolean correct;
}
