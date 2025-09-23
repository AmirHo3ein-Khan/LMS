package ir.lms.service;

import ir.lms.model.OfferedCourse;
import ir.lms.service.base.BaseService;

import java.security.Principal;
import java.util.List;

public interface OfferedCourseService extends BaseService<OfferedCourse, Long> {
    List<OfferedCourse> findAllTeacherCourse(Principal principal);
    List<OfferedCourse> findAllStudentCourses(Principal principal);
    List<OfferedCourse> findAllTermCourses(Long termId, Principal principal);


}
