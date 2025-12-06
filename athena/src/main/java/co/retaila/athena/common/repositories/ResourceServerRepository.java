package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.ResourceServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceServerRepository extends JpaRepository<ResourceServer, Long>, QuerydslPredicateExecutor<ResourceServer> {

    boolean existsByResourceId(String resourceId);
    List<ResourceServer> findByResourceIdIn(List<String> resourceIds);

}