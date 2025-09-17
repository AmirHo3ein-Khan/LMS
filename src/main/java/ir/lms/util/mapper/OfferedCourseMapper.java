package ir.lms.util.mapper;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.*;
import ir.lms.repository.CourseRepository;
import ir.lms.repository.MajorRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.TermRepository;
import ir.lms.util.dto.auth.PersonDTO;
import ir.lms.util.mapper.base.BaseMapper;
import ir.lms.util.dto.offeredCourse.OfferedCourseDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OfferedCourseMapper implements BaseMapper<OfferedCourse , OfferedCourseDTO> {
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;

    public abstract OfferedCourseDTO toDto(OfferedCourse entity);

    public abstract OfferedCourse toEntity(OfferedCourseDTO dto);

    @AfterMapping
    protected void afterToEntity(OfferedCourseDTO dto, @MappingTarget OfferedCourse entity) {
        if (dto.getTermId() != null) {
            Term term = termRepository.findById(dto.getTermId())
                    .orElseThrow(() -> new EntityNotFoundException("not found"));
            entity.setTerm(term);
        }
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("not found"));
            entity.setCourse(course);
        }
        if (dto.getTeacherId() != null) {
            Person person = personRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new EntityNotFoundException("not found"));
            entity.setTeacher(person);
        }
    }

    @AfterMapping
    protected void afterToDTO(OfferedCourse entity, @MappingTarget OfferedCourseDTO dto) {
        if (entity.getTerm() != null) {
            dto.setTermId(entity.getTerm().getId());
        }
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
        }
        if (entity.getTeacher() != null) {
            dto.setTeacherId(entity.getTeacher().getId());
        }
    }
}
