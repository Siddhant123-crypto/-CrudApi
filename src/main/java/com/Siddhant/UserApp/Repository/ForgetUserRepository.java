package com.Siddhant.UserApp.Repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Siddhant.UserApp.Entity.User;
@Repository
public interface ForgetUserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
}