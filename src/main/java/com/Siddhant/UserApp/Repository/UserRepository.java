package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Optional<User> findFirstByMobile(String mobile);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByUserId(UUID userId);

    List<User> findByRoleAndStatusAndIsActiveTrue(Role role, Status status);

    List<User> findByRoleAndStatusAndIsActiveFalse(Role role, Status status);

    @Query("""
    SELECT u FROM User u
    WHERE u.role = com.Siddhant.UserApp.Entity.Role.CUSTOMER
    AND (
        LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR u.mobile LIKE CONCAT('%', :keyword, '%')
    )
""")
    List<User> searchCustomers(@Param("keyword") String keyword);

    @Query("""
    SELECT u FROM User u
    WHERE u.role = com.Siddhant.UserApp.Entity.Role.CUSTOMER
    AND (:state IS NULL OR LOWER(u.state) = LOWER(:state))
    AND (:status IS NULL OR u.status = :status)
""")
    List<User> filterCustomers(
            @Param("state") String state,
            @Param("status") Status status
    );
    long countByRole(Role role);

    long countByRoleAndStatus(Role role, Status status);

    long countByRoleAndIsActiveTrue(Role role);

    long countByRoleAndIsActiveFalse(Role role);

    List<User> findTop5ByRoleInOrderByCreatedOnDesc(List<Role> roles);
}
