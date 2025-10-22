package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Course extends BaseEntity<Long> {

    private String title;

    private boolean deleted;

    private Integer unit;

    private String description;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @OneToMany(mappedBy = "course")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Question> questions = new ArrayList<>();
}
