package ir.lms.util.dto.major;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MajorDTO {

    @NotNull(message = "unit graduated number cannot be null")
    private Integer unitNumberGraduated;

    @NotBlank(message = "Major cannot be empty")
    @NotNull(message = "Major cannot be null")
    private String majorName;
}
