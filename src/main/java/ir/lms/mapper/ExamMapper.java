package ir.lms.mapper;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.ExamTemplate;
import ir.lms.model.OfferedCourse;
import ir.lms.repository.OfferedCourseRepository;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.dto.exam.ExamDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ExamMapper implements BaseMapper<ExamTemplate, ExamDTO> {

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    public abstract ExamDTO toDto(ExamTemplate entity);

    public abstract ExamTemplate toEntity(ExamDTO dto);

    @AfterMapping
    protected void afterToEntity(ExamDTO dto, @MappingTarget ExamTemplate entity) {
        if (dto.getCourseId() != null) {
            OfferedCourse offeredCourse = offeredCourseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            entity.setOfferedCourse(offeredCourse);
        }
    }

    @AfterMapping
    protected void afterToDTO(ExamTemplate entity, @MappingTarget ExamDTO dto) {
        if (entity.getOfferedCourse() != null) {
            dto.setCourseId(entity.getOfferedCourse().getId());
        }
    }
}
