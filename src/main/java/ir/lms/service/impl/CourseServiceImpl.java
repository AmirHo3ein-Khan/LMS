package ir.lms.service.impl;

import ir.lms.model.Course;
import ir.lms.repository.CourseRepository;
import ir.lms.service.CourseService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.dto.course.CourseDTO;
import ir.lms.util.dto.mapper.CourseMapper;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends BaseServiceImpl<Course, CourseDTO, Long> implements CourseService {


    protected CourseServiceImpl(CourseRepository repository , CourseMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected Course updateEntity(Course entity, CourseDTO courseDTO) {
        entity.setDescription(courseDTO.getDescription());
        entity.setUnit(courseDTO.getUnit());
        entity.setTitle(courseDTO.getTitle());
        return entity;
    }
}
