package ir.lms.util.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
