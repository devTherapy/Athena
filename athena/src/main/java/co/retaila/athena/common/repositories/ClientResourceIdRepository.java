package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientResourceId;
import co.retaila.athena.common.entities.ResourceServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientResourceIdRepository extends JpaRepository<ClientResourceId, Long> {

    boolean existsByClientAndResourceServer(Client client, ResourceServer resourceServer);
    List<ClientResourceId> findByClient(Client client);
    @Modifying
    @Transactional
    @Query("DELETE FROM ClientResourceId cr WHERE cr.client.id = ?1 AND cr.resourceServer.id IN ?2")
    void deleteByClientAndResourceServerIn(Long clientId, List<Long> resourceServerIds);

}