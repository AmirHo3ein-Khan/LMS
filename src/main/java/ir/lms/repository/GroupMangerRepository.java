package ir.lms.repository;

import ir.lms.model.GroupManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMangerRepository extends JpaRepository<GroupManager,Long> {
}
