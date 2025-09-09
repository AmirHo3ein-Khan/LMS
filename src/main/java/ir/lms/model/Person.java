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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(unique = true)
    @NotBlank(message = "National code cannot be empty")
    @NotNull(message = "National code cannot be null")
    private String phoneNumber;

    @ManyToMany(mappedBy = "persons" , fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "person", cascade = CascadeType.PERSIST)
    private Account account;

    @OneToMany(mappedBy = "person")
    private List<ExamInstance> exams = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "person_courses")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();

    @OneToOne
    private Major major;
}
