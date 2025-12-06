package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientDomain;
import co.retaila.athena.common.entities.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientDomainRepository extends JpaRepository<ClientDomain, Long> {

    boolean existsByClientAndDomain(Client client, Domain domain);
    List<ClientDomain> findByClient(Client client);
    @Modifying
    @Transactional
    @Query("DELETE FROM ClientDomain cd WHERE cd.client.id = ?1 AND cd.domain.id IN ?2")
    void deleteByClientAndDomainIn(Long clientId, List<Long> domainIds);

}