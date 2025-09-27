package ir.lms.repository;

import ir.lms.model.Person;
import ir.lms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {
    boolean existsByNationalCode(String nationalCode);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByRolesContains(Role role);
    @Query("""
                SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
                FROM Person p
                JOIN p.offeredCourses c
                WHERE p.id = :studentId AND c.id = :courseId""")
    boolean isStudentAssignedToCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
