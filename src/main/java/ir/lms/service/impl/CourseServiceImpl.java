package ir.lms.service.impl;

import ir.lms.exception.DuplicateException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.Course;
import ir.lms.model.Major;
import ir.lms.repository.CourseRepository;
import ir.lms.service.CourseService;
import ir.lms.service.MajorService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends BaseServiceImpl<Course, Long> implements CourseService {
    private final static String EXIST_COURSE = "This course already exists in this major!";
    private final static String NOT_FOUND = "%s not found!";

    private final CourseRepository courseRepository;
    private final MajorService majorService;

    protected CourseServiceImpl(JpaRepository<Course, Long> repository, CourseRepository courseRepository, MajorService majorService) {
        super(repository);
        this.courseRepository = courseRepository;
        this.majorService = majorService;
    }


    @Override
    protected void prePersist(Course course) {
        Major major = majorService.findById(course.getMajor().getId());
        if (!major.getActive()) {
            throw new EntityNotFoundException(String.format(NOT_FOUND, "Major"));
        }
        if (courseRepository.existsByMajorAndTitle(course.getMajor().getId() , course.getTitle())) {
            throw new DuplicateException(EXIST_COURSE);
        }
        course.setActive(true);
    }

    @Override
    public void delete(Long aLong) {
        Course course = courseRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));
        course.setActive(false);
        courseRepository.save(course);
    }
}
