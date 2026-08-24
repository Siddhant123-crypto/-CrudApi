package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
}