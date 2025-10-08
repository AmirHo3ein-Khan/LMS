package ir.lms.util.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
        private String firstName;
        private String lastName;
        private String nationalCode ;
        private String phoneNumber;
        private String majorName;
}
