package ir.lms.repository;

import ir.lms.model.Person;
import ir.lms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> , JpaSpecificationExecutor<Person> {
    boolean existsByNationalCodeAndPhoneNumber(String nationalCode, String phoneNumber);
    boolean existsByRolesContains(Role role);
    boolean existsByIdAndOfferedCourses_Id(Long studentId, Long courseId);
    List<Person> findAllByMajor_MajorName(String majorName);
}
