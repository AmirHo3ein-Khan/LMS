package ir.lms.mapper;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.Major;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.repository.MajorRepository;
import ir.lms.repository.RoleRepository;
import ir.lms.dto.auth.PersonDTO;
import ir.lms.dto.auth.RegisterDTO;
import ir.lms.mapper.base.BaseMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RegisterMapper implements BaseMapper<Person ,RegisterDTO>{

    @Autowired
    private MajorRepository majorRepository;

    public abstract RegisterDTO toDto(Person entity);

    public abstract Person toEntity(PersonDTO dto);

    @AfterMapping
    protected void afterToEntity(RegisterDTO dto, @MappingTarget Person person) {
        if (dto.getMajorName() != null) {
            Major major = majorRepository
                    .findByMajorName(dto.getMajorName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Major with name " + dto.getMajorName() + " not found"
                    ));
            person.setMajor(major);
        }
    }

    @AfterMapping
    protected void afterToDTO(Person person, @MappingTarget RegisterDTO dto) {
        if (person.getMajor().getMajorName() != null) {
            Major major = majorRepository
                    .findByMajorName(person.getMajor().getMajorName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Major with name " + dto.getMajorName() + " not found"
                    ));
            dto.setMajorName(major.getMajorName());
        }
    }
}
