package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.RoleAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RoleAuthorityRepository extends JpaRepository<RoleAuthority, Long> {

    boolean existsByRoleAndAuthority(Role role, Authority authority);
    List<RoleAuthority> findByRole(Role role);
    @Modifying
    @Transactional
    @Query("DELETE FROM RoleAuthority ra WHERE ra.role.id = ?1 AND ra.authority.id IN ?2")
    void deleteByRoleAndAuthorityIn(Long id, List<Long> authorityIds);

}