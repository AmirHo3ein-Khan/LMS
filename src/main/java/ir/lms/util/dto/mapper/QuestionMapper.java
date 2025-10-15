package ir.lms.util.dto.mapper;

import ir.lms.util.dto.OptionDTO;
import ir.lms.util.dto.QuestionDTO;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.util.dto.mapper.base.BaseMapper;
import ir.lms.model.*;
import ir.lms.repository.CourseRepository;
import ir.lms.repository.MajorRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class QuestionMapper implements BaseMapper<Question, QuestionDTO> {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private OptionMapper optionMapper;

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
            Course course = courseRepository.findByTitleAndMajor(dto.getCourseName(), major)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Course with name " + dto.getCourseName() + " not found in " + major.getMajorName() + " major"
                    ));

            entity.setCourse(course);
        }
    }

    @AfterMapping
    protected void afterToDTO(Question entity, @MappingTarget QuestionDTO dto) {
        if (entity.getCourse() != null) {
            if (entity instanceof TestQuestion) {
                dto.setQuestionType("TEST");
                List<Option> options = ((TestQuestion) entity).getOptions();
                List<OptionDTO> optionDTOList = new ArrayList<>();
                for (Option option : options) {
                    optionDTOList.add(optionMapper.toDto(option));
                }
                dto.setOptions(optionDTOList);
            }
            if (entity instanceof DescriptiveQuestion) {
                dto.setQuestionType("DESCRIPTIVE");
            }
            dto.setCourseName(entity.getCourse().getTitle());
            dto.setMajorName(entity.getCourse().getMajor().getMajorName());
        }
    }
}
