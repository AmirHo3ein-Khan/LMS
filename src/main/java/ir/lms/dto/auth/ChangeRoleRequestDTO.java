package ir.lms.dto.auth;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequestDTO {
    private String role;
}
