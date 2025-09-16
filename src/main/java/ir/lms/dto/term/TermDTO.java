package ir.lms.dto.term;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TermDTO {
    private String termName;
    private String unitNumberGraduated;
}
