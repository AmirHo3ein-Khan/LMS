package ir.lms.util.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TermDTO {
    private String majorName;
    private LocalDate courseRegistrationStart;
    private LocalDate courseRegistrationEnd;
    private LocalDate classesStartDate;
    private LocalDate classesEndDate;
}
