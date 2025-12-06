package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {

    List<AuthCode> findByClient(Client client);
    @Modifying
    @Transactional
    @Query("UPDATE AuthCode ac SET ac.isUsed = true WHERE ac.id = ?1")
    void markAuthCodeAsUsed(Long id);

}