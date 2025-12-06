package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Revocation;
import co.retaila.athena.common.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RevocationRepository extends JpaRepository<Revocation, Long> {

    boolean existsByUserAndAuthority(User user, Authority authority);
    List<Revocation> findByUser(User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM Revocation r WHERE r.user.id = ?1 AND r.authority.id IN ?2")
    void deleteByUserAndAuthorityIn(Long userId, List<Long> authorityIds);

}