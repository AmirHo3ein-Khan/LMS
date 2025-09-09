package ir.lms.util.dto.auth;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
        private String nationalCode ;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
}
