package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DeviceCodeRepository extends JpaRepository<DeviceCode, Long> {

    List<DeviceCode> findByClient(Client client);
    @Query("SELECT dc FROM DeviceCode dc WHERE dc.isUsed = false")
    List<DeviceCode> fetchUnusedDeviceCodes();
    @Modifying
    @Transactional
    @Query("UPDATE DeviceCode dc SET dc.isUsed = true WHERE dc.id = ?1")
    void markDeviceCodeAsUsed(Long id);

}