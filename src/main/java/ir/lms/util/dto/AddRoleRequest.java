package ir.lms.util.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleRequest{
    @NotNull(message = "Person ID is required")
    @Min(value = 1, message = "Person ID must be a positive number")
    private Long personId;

    @NotBlank(message = "Role is required")
    private String role;
}
