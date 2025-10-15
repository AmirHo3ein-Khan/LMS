package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.Semester;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Term extends BaseEntity<Long> {

    @Column(name = "term-year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    private boolean deleted;

    @OneToOne(cascade = CascadeType.ALL)
    private AcademicCalender academicCalender;

    @ManyToOne
    private Major major;

    @OneToMany(mappedBy = "term")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();
}
