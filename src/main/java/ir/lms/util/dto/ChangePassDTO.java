package ir.lms.util.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassDTO {
    private String newPassword;
    private String oldPassword;
}
