package ir.lms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("MULTIPLE_CHOICE_ANSWER")
public class TestAnswer extends Answer {
    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;
}
