package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "title cannot be empty")
    @NotNull(message = "title cannot be null")
    private String title;

    @Size(min = 1, max = 10)
    private Integer unit;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @OneToMany(mappedBy = "course")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Question> questions = new ArrayList<>();
}
