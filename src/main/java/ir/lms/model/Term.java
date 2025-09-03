package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.Semester;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Term extends BaseEntity<Long> {

    @NotNull(message = "Term year cannot be null.")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Semester cannot be null.")
    private Semester semester;


    private LocalDate date;

    @OneToMany(mappedBy = "term")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();
}
