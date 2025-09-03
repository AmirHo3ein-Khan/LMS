package ir.lms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@SuperBuilder
@DiscriminatorValue("STUDENT")
public class Student extends Person {
    @ManyToMany(mappedBy = "students")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<ExamInstance> exams = new ArrayList<>();
}
