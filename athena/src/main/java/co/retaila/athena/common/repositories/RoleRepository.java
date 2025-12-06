package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, QuerydslPredicateExecutor<Role> {

    boolean existsByNameAndDomain(String role, Domain domain);
    Optional<Role> findByNameAndDomain(String role, Domain domain);
    Optional<Role> findByNameAndDomainName(String role, String domain);
    List<Role> findByNameInAndDomainName(List<String> roleNames, String domain);

}