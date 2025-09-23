package ir.lms.service;

import ir.lms.model.Course;
import ir.lms.service.base.BaseService;

import java.util.List;

public interface CourseService extends BaseService<Course, Long> {
    List<Course> findAllMajorCourses(String majorName);
}
