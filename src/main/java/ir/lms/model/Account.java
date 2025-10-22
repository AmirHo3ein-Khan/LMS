package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.RegisterState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity<Long> {

    @Column(unique = true)
    private UUID authId;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private RegisterState state;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    private Role activeRole;

}
