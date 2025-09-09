package ir.lms.util.dto.auth;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChooseRoleRequestDTO {
    private String role;
}
