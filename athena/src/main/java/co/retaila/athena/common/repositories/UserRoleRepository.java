package co.retaila.athena.common.repositories;

import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    boolean existsByUserAndRole(User user, Role role);
    List<UserRole> findByUser(User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = ?1 AND ur.role.id IN ?2")
    void deleteByUserAndRoleIn(Long userId, List<Long> roleIds);

}