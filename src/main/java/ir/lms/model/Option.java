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
public class Option extends BaseEntity<Long> {
    @Column(nullable = false)
    private String optionText;

    @Column(nullable = false)
    private boolean correct;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private TestQuestion question;

    @OneToMany(mappedBy = "option")
    private List<TestAnswer> answers = new ArrayList<>();

}
