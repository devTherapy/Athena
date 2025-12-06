package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientAuthorityRepository extends JpaRepository<ClientAuthority, Long> {

    boolean existsByClientAndAuthority(Client client, Authority authority);
    List<ClientAuthority> findByClient(Client client);
    @Modifying
    @Transactional
    @Query("DELETE FROM ClientAuthority ca WHERE ca.client.id = ?1 AND ca.authority.id IN ?2")
    void deleteByClientAndAuthorityIn(Long id, List<Long> authorityIds);

}