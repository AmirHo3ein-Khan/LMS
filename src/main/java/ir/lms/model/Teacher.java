package ir.lms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("TEACHER")
public class Teacher extends Person {

    @OneToMany(mappedBy = "teacher")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();
}
