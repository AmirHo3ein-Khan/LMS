package ir.lms.util.dto.mapper;

import ir.lms.model.Major;
import ir.lms.model.Person;
import ir.lms.util.dto.mapper.base.BaseMapper;
import ir.lms.util.dto.PersonDTO;
import ir.lms.repository.MajorRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PersonMapper implements BaseMapper<Person, PersonDTO> {
    @Autowired
    private MajorRepository majorRepository;

    public abstract PersonDTO toDto(Person entity);

    public abstract Person toEntity(PersonDTO dto);

    @AfterMapping
    protected void afterToEntity(PersonDTO dto, @MappingTarget Person person) {
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
    protected void afterToDTO(Person person, @MappingTarget PersonDTO dto) {
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
