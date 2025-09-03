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

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Person extends BaseEntity<Long> {

    @NotBlank(message = "first name cannot be empty")
    @NotNull(message = "first name cannot be null")
    private String firstName;

    @NotBlank(message = "lastname cannot be empty")
    @NotNull(message = "lastname cannot be null")
    private String lastName;

    @Column(unique = true)
    @NotBlank(message = "National code cannot be empty")
    @NotNull(message = "National code cannot be null")
    private String nationalCode;

    @ManyToMany
    @JoinTable(name = "PERSON_ROLE")
    private List<Role> roles = new ArrayList<>();

    @OneToOne
    private Account account;
}
