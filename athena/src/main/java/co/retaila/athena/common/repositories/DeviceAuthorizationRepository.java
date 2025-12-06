package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.DeviceAuthorization;
import co.retaila.athena.common.entities.DeviceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceAuthorizationRepository extends JpaRepository<DeviceAuthorization, Long> {

    Optional<DeviceAuthorization> findByDeviceCode(DeviceCode deviceCode);

}