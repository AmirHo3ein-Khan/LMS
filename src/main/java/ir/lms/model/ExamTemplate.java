package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.ExamState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExamTemplate extends BaseEntity<Long> {

    private String title;

    private String description;

    private Instant examStartTime;

    private Instant examEndTime;

    @Enumerated(EnumType.STRING)
    private ExamState examState;

    private Double examScore;

    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private OfferedCourse offeredCourse;

    @OneToMany(mappedBy = "exam")
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "exam")
    private List<ExamInstance> examInstance = new ArrayList<>();
}
