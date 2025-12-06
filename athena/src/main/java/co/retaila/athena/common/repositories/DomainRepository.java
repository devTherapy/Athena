package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long>, QuerydslPredicateExecutor<Domain> {

    Optional<Domain> findByName(String name);
    List<Domain> findByNameIn(List<String> domainNames);
    boolean existsByName(String name);

}