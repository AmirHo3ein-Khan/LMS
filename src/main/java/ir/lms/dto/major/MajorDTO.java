package ir.lms.dto.major;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MajorDTO {

    @NotBlank(message = "Major cannot be empty")
    @NotNull(message = "Major cannot be null")
    private String majorName;
}
