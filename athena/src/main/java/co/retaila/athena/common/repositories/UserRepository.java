package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    boolean existsByUsernameAndDomainName(String username, String domain);
    Optional<User> findByUsernameAndDomainName(String username, String domain);

}
