package ir.lms.dto.course;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

    @NotBlank(message = "Course title cannot be empty")
    @Size(min = 3, max = 30, message = "Title must be between 3 and 30 characters")
    private String title;

    @NotNull(message = "Unit cannot be empty")
    @Min(value = 1, message = "Unit must be at least 1")
    private Integer unit;

    @Size(max = 50 , message = "description should not more than 50 character!")
    private String description;

    @NotNull(message = "Unit cannot be empty")
    private String majorName;
}

