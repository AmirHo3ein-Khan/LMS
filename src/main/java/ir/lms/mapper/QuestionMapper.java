package ir.lms.mapper;

import ir.lms.dto.question.QuestionDTO;
import ir.lms.dto.term.TermDTO;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.model.Course;
import ir.lms.model.Major;
import ir.lms.model.Question;
import ir.lms.model.Term;
import ir.lms.repository.CourseRepository;
import ir.lms.repository.MajorRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class QuestionMapper implements BaseMapper<Question, QuestionDTO> {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MajorRepository majorRepository;

    public abstract QuestionDTO toDto(Question entity);

    public abstract Question toEntity(QuestionDTO dto);

    @AfterMapping
    protected void afterToEntity(QuestionDTO dto, @MappingTarget Question entity) {
        if (dto.getCourseName() != null && dto.getMajorName() != null) {
            Major major = majorRepository
                    .findByMajorName(dto.getMajorName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Major with name " + dto.getMajorName() + " not found"
                    ));
            Course course = courseRepository.findByMajor(major)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Course with name " + dto.getCourseName() + " not found"
                    ));
            if (courseRepository.existsByMajorAndTitle(major.getId(), dto.getCourseName())) {
                entity.setCourse(course);
            }
        }
    }

    @AfterMapping
    protected void afterToDTO(Question entity, @MappingTarget QuestionDTO dto) {
        if (entity.getCourse() != null) {
            dto.setCourseName(entity.getCourse().getTitle());
            dto.setMajorName(entity.getCourse().getMajor().getMajorName());
        }
    }
}
