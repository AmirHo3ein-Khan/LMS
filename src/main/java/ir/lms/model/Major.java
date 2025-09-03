package ir.lms.model;

import ir.lms.model.base.BaseEntity;import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    @NotBlank(message = "Major cannot be empty")
    @NotNull(message = "Major cannot be null")
    private String majorName;

    @OneToOne(mappedBy = "major")
    private GroupManager groupManager;

    @OneToMany(mappedBy = "major")
    private List<Course> courses = new ArrayList<>();
}
