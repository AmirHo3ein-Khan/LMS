package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.ExamInstanceStatus;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ExamInstance extends BaseEntity<Long> {

    @NotNull(message = "Start date of exam for student cannot be null")
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private ExamInstanceStatus status;

    private double totalScore;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private ExamTemplate exam;

    @OneToMany(mappedBy = "examInstance")
    private List<Answer> answers = new ArrayList<>();

}
