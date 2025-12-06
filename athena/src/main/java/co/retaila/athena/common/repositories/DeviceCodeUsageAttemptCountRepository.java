package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.DeviceCodeUsageAttemptCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface DeviceCodeUsageAttemptCountRepository extends JpaRepository<DeviceCodeUsageAttemptCount, Long> {

    Optional<DeviceCodeUsageAttemptCount> findByDeviceCode(DeviceCode deviceCode);
    @Modifying
    @Transactional
    @Query("UPDATE DeviceCodeUsageAttemptCount c SET c.attemptCount = c.attemptCount + 1 WHERE c.deviceCode.id = ?1")
    void incrementAttemptCount(Long deviceCodeId);

}