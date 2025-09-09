package ir.lms.util.dto.auth;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleRequest{
    private Long personId;
    private String role;
}
