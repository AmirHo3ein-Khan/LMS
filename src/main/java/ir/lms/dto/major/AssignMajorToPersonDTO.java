package ir.lms.dto.major;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignMajorToPersonDTO {
    private Long personId;
    private Long majorId;
}
