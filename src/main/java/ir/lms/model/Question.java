package ir.lms.model;

import ir.lms.model.base.BaseEntity;import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "question")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
public class Question extends BaseEntity<Long> {

    @NotBlank(message = "Title cannot be empty")
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotBlank(message = "Question text cannot be empty")
    @NotNull(message = "Question text cannot be null")
    private String questionText;

    private double defaultScore;

    @OneToMany(mappedBy = "question")
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
