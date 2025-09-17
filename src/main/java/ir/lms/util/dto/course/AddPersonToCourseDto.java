package ir.lms.util.dto.course;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddPersonToCourseDto {
    private Long courseId;
    private Long personId;
}
