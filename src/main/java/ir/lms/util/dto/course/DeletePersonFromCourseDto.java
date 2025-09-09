package ir.lms.util.dto.course;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeletePersonFromCourseDto {
    private Long courseId;
    private Long studentId;
}
