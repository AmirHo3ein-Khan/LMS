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

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String nationalCode;

    @Column(unique = true)
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_roles")
    private List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "person")
    private Account account;

    @OneToMany(mappedBy = "person")
    private List<ExamInstance> exams = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "person_courses")
    private List<OfferedCourse> offeredCourses = new ArrayList<>();

    @ManyToOne
    private Major major;
}
