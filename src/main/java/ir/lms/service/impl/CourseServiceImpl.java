package ir.lms.service.impl;

import ir.lms.exception.DuplicateException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.Course;
import ir.lms.model.Major;
import ir.lms.repository.CourseRepository;
import ir.lms.repository.MajorRepository;
import ir.lms.service.CourseService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl extends BaseServiceImpl<Course, Long> implements CourseService {
    private final static String EXIST_COURSE = "This course already exists in this major!";
    private final static String NOT_FOUND = "%s not found!";

    private final CourseRepository courseRepository;
    private final MajorRepository majorRepository;

    protected CourseServiceImpl(JpaRepository<Course, Long> repository, CourseRepository courseRepository, MajorRepository majorRepository) {
        super(repository);
        this.courseRepository = courseRepository;
        this.majorRepository = majorRepository;
    }


    @Override
    protected void prePersist(Course course) {
        Major major = majorRepository.findById(course.getMajor().getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Major")));
        if (major.isDeleted()) {
            throw new EntityNotFoundException(String.format(NOT_FOUND, "Major"));
        }
        if (courseRepository.existsByMajorAndTitle(course.getMajor().getId() , course.getTitle())) {
            throw new DuplicateException(EXIST_COURSE);
        }
        course.setDeleted(false);
    }

    @Override
    public void delete(Long aLong) {
        Course course = courseRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));
        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public List<Course> findAllMajorCourses(String majorName) {
        Major major = majorRepository.findByMajorName(majorName)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Major")));
        return major.getCourses();
    }

    @Override
    public Course findById(Long aLong) {
        Course course = courseRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));
        if (course.isDeleted()) {
            throw new EntityNotFoundException(String.format(NOT_FOUND, "Course"));
        }
        return course;
    }

    @Override
    public List<Course> findAll() {
        List<Course> courses = courseRepository.findAll();
        List<Course> result = new ArrayList<>();
        for (Course course : courses) {
            if (!course.isDeleted()) {
                result.add(course);
            }
        }
        return result;
    }

    @Override
    public Course update(Long aLong, Course course) {
        Course foundedCourse = courseRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));
        foundedCourse.setTitle(course.getTitle());
        foundedCourse.setMajor(course.getMajor());
        foundedCourse.setUnit(course.getUnit());
        foundedCourse.setDescription(course.getDescription());
        return courseRepository.save(foundedCourse);
    }
}
