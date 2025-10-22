package ir.lms.util.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TermDTO {
    @NotBlank(message = "Major name cannot be empty")
    private String majorName;

    @NotNull(message = "Course registration start date is required")
    @FutureOrPresent(message = "Course registration start date should be today or in the future")
    private LocalDate courseRegistrationStart;

    @NotNull(message = "Course registration end date is required")
    @FutureOrPresent(message = "Course registration end date should be today or in the future")
    private LocalDate courseRegistrationEnd;

    @NotNull(message = "Classes start date is required")
    @FutureOrPresent(message = "Classes start date should be today or in the future")
    private LocalDate classesStartDate;

    @NotNull(message = "Classes end date is required")
    @FutureOrPresent(message = "Classes end date should be today or in the future")
    private LocalDate classesEndDate;

    @AssertTrue(message = "Course registration end date must be after start date")
    public boolean isRegistrationDatesValid() {
        if (courseRegistrationStart == null || courseRegistrationEnd == null) return true;
        return courseRegistrationEnd.isAfter(courseRegistrationStart);
    }

    @AssertTrue(message = "Classes end date must be after start date")
    public boolean isClassesDatesValid() {
        if (classesStartDate == null || classesEndDate == null) return true;
        return classesEndDate.isAfter(classesStartDate);
    }

//    @AssertTrue(message = "Classes should start after course registration ends")
//    public boolean isClassesStartAfterRegistrationEnd() {
//        if (courseRegistrationEnd == null || classesStartDate == null) return true;
//        return classesStartDate.isAfter(courseRegistrationEnd);
//    }
}
