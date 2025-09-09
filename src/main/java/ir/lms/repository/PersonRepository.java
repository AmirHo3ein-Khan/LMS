package ir.lms.repository;

import ir.lms.model.Person;
import ir.lms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {
    boolean existsByNationalCode(String nationalCode);
    boolean existsByRolesContains(Role role);
}
