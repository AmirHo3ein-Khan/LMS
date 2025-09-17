package ir.lms.repository;

import ir.lms.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByAuthId(UUID authId);
    boolean existsByUsername(String username);
}
