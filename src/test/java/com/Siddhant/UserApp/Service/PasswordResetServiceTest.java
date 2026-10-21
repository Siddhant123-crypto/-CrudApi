package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.PasswordResetToken;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.PasswordResetTokenRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.dto.ForgotPasswordRequest;
import com.Siddhant.UserApp.dto.ResetPasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                userRepository,
                tokenRepository,
                mailSender,
                passwordEncoder
        );
    }

    @Test
    void testForgotPassword_NullOrEmptyEmail_ThrowsException() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.forgotPassword(request);
        });

        assertEquals("Email is required", ex.getMessage());
        verifyNoInteractions(mailSender);
    }

    @Test
    void testForgotPassword_EmailNotRegistered_ReturnsGenericMessageWithoutSendingMail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        String message = passwordResetService.forgotPassword(request);

        assertEquals("If the email is registered, a reset token will be sent.", message);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testForgotPassword_EmailRegistered_SavesTokenAndSendsMail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findAll()).thenReturn(Collections.emptyList());

        String message = passwordResetService.forgotPassword(request);

        assertEquals("If the email is registered, a reset token will be sent.", message);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals("test@example.com", savedToken.getEmail());
        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getExpiryDateTime().isAfter(LocalDateTime.now()));

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPassword_InvalidToken_ThrowsException() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("invalid-token");
        request.setNewPassword("NewSecurePassword123!");

        when(tokenRepository.findById("invalid-token")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword(request);
        });

        assertEquals("Invalid or expired reset token", ex.getMessage());
    }

    @Test
    void testResetPassword_ExpiredToken_DeletesTokenAndThrowsException() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("expired-token");
        request.setNewPassword("NewSecurePassword123!");

        PasswordResetToken token = new PasswordResetToken(
                "expired-token",
                "test@example.com",
                LocalDateTime.now().minusMinutes(5)
        );

        when(tokenRepository.findById("expired-token")).thenReturn(Optional.of(token));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword(request);
        });

        assertEquals("Invalid or expired reset token", ex.getMessage());
        verify(tokenRepository).delete(token);
    }

    @Test
    void testResetPassword_ValidToken_EncodesPasswordAndDeletesToken() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("valid-token");
        request.setNewPassword("NewSecurePassword123!");

        PasswordResetToken token = new PasswordResetToken(
                "valid-token",
                "test@example.com",
                LocalDateTime.now().plusMinutes(10)
        );

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldPlainPassword");

        when(tokenRepository.findById("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewSecurePassword123!")).thenReturn("$2a$10$encodedHash12345");

        String result = passwordResetService.resetPassword(request);

        assertEquals("Password reset successful. Please login again.", result);
        assertEquals("$2a$10$encodedHash12345", user.getPassword());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(token); // Ensures token cannot be reused
    }
}
