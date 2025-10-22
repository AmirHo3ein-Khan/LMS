package ir.lms.repository;

import ir.lms.model.Major;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major,Long> {
    Optional<Major> findByMajorName(String majorName);

    List<Major> findByDeletedIsFalse();
}
