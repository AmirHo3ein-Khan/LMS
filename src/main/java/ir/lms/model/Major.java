package ir.lms.model;

import ir.lms.model.base.BaseEntity;
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
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Major extends BaseEntity<Long> {

    @NotBlank(message = "Major cannot be empty")
    @NotNull(message = "Major cannot be null")
    @Column(unique = true)
    private String majorName;

    private UUID majorCode;

    private Boolean active;

    @OneToMany(mappedBy = "major")
    private List<Person> person = new ArrayList<>();

    @OneToMany(mappedBy = "major")
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "major")
    private List<Term> terms = new ArrayList<>();
}
