package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, Long>, QuerydslPredicateExecutor<Scope> {

    boolean existsByNameAndDomain(String scope, Domain domain);
    Optional<Scope> findByNameAndDomainName(String scope, String domain);
    List<Scope> findByNameInAndDomain(List<String> scopeNames, Domain domain);

}