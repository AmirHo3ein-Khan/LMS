package ir.lms.mapper;

import ir.lms.model.OfferedCourse;
import ir.lms.dto.offeredCourse.ResponseOfferedCourseDTO;
import ir.lms.mapper.base.BaseMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ResponseOfferedCourseMapper implements BaseMapper<OfferedCourse , ResponseOfferedCourseDTO> {

    public abstract ResponseOfferedCourseDTO toDto(OfferedCourse entity);

    public abstract OfferedCourse toEntity(ResponseOfferedCourseDTO dto);


    @AfterMapping
    protected void afterToDTO(OfferedCourse entity, @MappingTarget ResponseOfferedCourseDTO dto) {
        if (entity.getTerm() != null) {
            dto.setTermId(entity.getTerm().getId());
        }
        if (entity.getCourse() != null) {
            dto.setCourseTitle(entity.getCourse().getTitle());
        }
        if (entity.getTeacher() != null) {
            dto.setTeacherName(entity.getTeacher().getFirstName() + " " + entity.getTeacher().getLastName());
        }
        if (entity.getTerm() != null) {
            dto.setMajorName(entity.getTerm().getMajor().getMajorName());
        }
    }
}
