package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, String> {
}
