package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>, QuerydslPredicateExecutor<Authority> {

    boolean existsByName(String name);
    boolean existsByNameAndDomainName(String authority, String domain);
    Optional<Authority> findByNameAndDomainName(String authority, String domain);
    List<Authority> findByNameInAndDomainName(List<String> authorities, String domain);
    List<Authority> findByNameInAndDomainNameIn(List<String> authorities, List<String> domains);

}