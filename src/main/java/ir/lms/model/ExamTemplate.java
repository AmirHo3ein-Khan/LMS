package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.ExamState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExamTemplate extends BaseEntity<Long> {

    @NotNull(message = "lastname cannot be null")
    private Integer examTime;

    @NotNull(message = "Exam start date cannot be null")
    private LocalDateTime examStartTime;

    @NotNull(message = "Exam start time cannot be null")
    private LocalDateTime examEndTime;

    @Enumerated(EnumType.STRING)
    private ExamState examState;

    private Double examScore;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private OfferedCourse offeredCourse;

    @OneToMany(mappedBy = "exam")
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "exam")
    private List<ExamInstance> exams = new ArrayList<>();
}
