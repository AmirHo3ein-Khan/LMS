package ir.lms.service;
import java.security.Principal;

public interface StudentService {
    void studentTakeCourse(Long courseId , Principal principal);
}
