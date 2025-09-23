package ir.lms.dto.offeredCourse;

import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOfferedCourseDTO {
    private Instant startTime;
    private Instant endTime;
    private Integer capacity;
    private String classLocation;
    private String courseTitle;
    private String teacherName;
    private String majorName;
    private Long termId;
}
