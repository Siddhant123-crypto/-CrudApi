package com.Siddhant.UserApp.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private LocalDateTime expiryDateTime;
}
