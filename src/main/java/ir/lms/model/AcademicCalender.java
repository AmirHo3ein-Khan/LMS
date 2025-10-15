package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicCalender extends BaseEntity<Long> {

    @OneToOne(mappedBy = "academicCalender")
    private Term term;

    private LocalDate courseRegistrationStart;

    private LocalDate courseRegistrationEnd;

    private LocalDate classesStartDate;

    private LocalDate classesEndDate;




}
