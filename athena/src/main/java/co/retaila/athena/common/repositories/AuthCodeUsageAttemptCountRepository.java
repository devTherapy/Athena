package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.AuthCodeUsageAttemptCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface AuthCodeUsageAttemptCountRepository extends JpaRepository<AuthCodeUsageAttemptCount, Long> {

    Optional<AuthCodeUsageAttemptCount> findByAuthCode(AuthCode authCode);
    @Modifying
    @Transactional
    @Query("UPDATE AuthCodeUsageAttemptCount c SET c.attemptCount = c.attemptCount + 1 WHERE c.authCode.id = ?1")
    void incrementAttemptCount(Long authCodeId);

}