package ir.lms.service;

import ir.lms.model.Major;
import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import ir.lms.service.base.BaseService;
import ir.lms.util.dto.offeredCourse.OfferedCourseDTO;

import java.util.List;

public interface OfferedCourseService extends BaseService<OfferedCourse, Long> {
    void assignCourseToStudent(Long course , Long studentId);
//    List<OfferedCourseDTO> getAllCoursesInATerm(Long termId , Long majorId);



}
