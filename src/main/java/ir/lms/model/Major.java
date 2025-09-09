package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.Degree;
import jakarta.persistence.*;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Major extends BaseEntity<Long> {

    @NotNull(message = "unit graduated number cannot be null")
    private Integer unitNumberGraduated;

    @NotBlank(message = "Major cannot be empty")
    @NotNull(message = "Major cannot be null")
    private String majorName;

    @OneToOne(mappedBy = "major")
    private Person person;

    @OneToMany(mappedBy = "major")
    private List<Course> courses = new ArrayList<>();
}
