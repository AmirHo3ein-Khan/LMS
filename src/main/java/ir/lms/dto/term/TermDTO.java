package ir.lms.dto.term;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TermDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String majorName;
}
