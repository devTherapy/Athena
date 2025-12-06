package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.common.entities.ScopeAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ScopeAuthorityRepository extends JpaRepository<ScopeAuthority, Long> {

    boolean existsByScopeAndAuthority(Scope scope, Authority authority);
    List<ScopeAuthority> findByScope(Scope scope);
    @Modifying
    @Transactional
    @Query("DELETE FROM ScopeAuthority ra WHERE ra.scope.id = ?1 AND ra.authority.id IN ?2")
    void deleteByScopeAndAuthorityIn(Long id, List<Long> authorityIds);

}