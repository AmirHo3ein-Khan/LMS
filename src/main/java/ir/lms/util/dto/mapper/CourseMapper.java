package ir.lms.util.dto.mapper;

import ir.lms.model.Course;
import ir.lms.util.BaseMapper;
import ir.lms.util.dto.course.CourseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CourseMapper extends BaseMapper<Course, CourseDTO> {
}
