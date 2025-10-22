package ir.lms.util.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequestDTO {
    @NotNull(message = "Role name required")
    @NotBlank(message = "Role name required")
    private String role;
}
