package ir.lms.model;

import ir.lms.model.base.BaseEntity;import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class ExamQuestion extends BaseEntity<Long> {

    private Double questionScore;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private ExamTemplate exam;

    @OneToMany(mappedBy = "examQuestion")
    private List<Answer> answers = new ArrayList<>();

}
