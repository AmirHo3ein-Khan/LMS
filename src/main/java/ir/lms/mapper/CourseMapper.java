package ir.lms.mapper;

import ir.lms.dto.course.CourseDTO;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.model.Course;
import ir.lms.model.Major;
import ir.lms.repository.MajorRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CourseMapper implements BaseMapper<Course, CourseDTO> {

    @Autowired
    private MajorRepository majorRepository;

    public abstract CourseDTO toDto(Course entity);

    public abstract Course toEntity(CourseDTO dto);

    @AfterMapping
    protected void afterToEntity(CourseDTO dto, @MappingTarget Course course) {
        if (dto.getMajorName() != null) {
            Major major = majorRepository
                .findByMajorName(dto.getMajorName())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Major with name " + dto.getMajorName() + " not found"
                ));
            course.setMajor(major);
        }
    }

    @AfterMapping
    protected void afterToDTO(Course course, @MappingTarget CourseDTO dto) {
        if (course.getMajor().getMajorName() != null) {
            Major major = majorRepository
                    .findByMajorName(course.getMajor().getMajorName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Major with name " + dto.getMajorName() + " not found"
                    ));
            dto.setMajorName(major.getMajorName());
        }
    }
}
