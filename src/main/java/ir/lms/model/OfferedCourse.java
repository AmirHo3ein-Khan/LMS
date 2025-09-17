package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OfferedCourse extends BaseEntity<Long> {

    private Instant startTime;

    private Instant endTime;

    private Integer capacity;

    private String classLocation;

    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Person teacher;

    @ManyToMany(mappedBy = "offeredCourses")
    private List<Person> student = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "term_id")
    private Term term;

    @OneToMany(mappedBy = "offeredCourse")
    private List<ExamTemplate> examTemplates = new ArrayList<>();
}
