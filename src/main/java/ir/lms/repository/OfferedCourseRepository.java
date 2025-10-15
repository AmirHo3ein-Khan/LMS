package ir.lms.repository;

import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Repository
public interface OfferedCourseRepository extends JpaRepository<OfferedCourse,Long> {
    @Query("SELECT SUM(oc.course.unit) " +
            "FROM OfferedCourse oc JOIN oc.students s " +
            "WHERE s.id = :studentId")
    Integer getTotalUnitsByPersonId(@Param("studentId") Long studentId);



    @Query("""
    select (count(oc2) > 0)
    from OfferedCourse oc2
    where oc2.teacher = :teacher
      and oc2.dayOfWeek = :dayOfWeek
      and oc2.classStartTime < :classEndTime
      and oc2.classEndTime > :classStartTime
    """)
    boolean existsOverlappingCourse(@Param("teacher") Person teacher,
                                    @Param("classStartTime") LocalTime classStartTime,
                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("classEndTime") LocalTime classEndTime);

}