package ir.lms.model;

import ir.lms.model.base.BaseEntity;
import ir.lms.model.enums.RegisterState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
    @NotBlank(message = "Email cannot be empty")
    @NotNull(message = "email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @Column(unique = true)
    @NotBlank(message = "Username cannot be empty")
    @NotNull(message = "Username cannot be null")
    @Size(min = 6, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;


    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8 ,message = "Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private RegisterState state;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne
    private Role activeRole;

    //todo session token : when login get session token(uuid), when login set , when logout remove (date) (has expired time)

    // inner log (crud login logout) elk log
}
