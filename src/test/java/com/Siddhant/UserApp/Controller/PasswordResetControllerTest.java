package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.PasswordResetService;
import com.Siddhant.UserApp.dto.ForgotPasswordRequest;
import com.Siddhant.UserApp.dto.ResetPasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordResetController).build();
    }

    @Test
    void testForgotPassword_Success() throws Exception {
        when(passwordResetService.forgotPassword(any(ForgotPasswordRequest.class)))
                .thenReturn("If the email is registered, a reset token will be sent.");

        mockMvc.perform(post("/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"farmer@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If the email is registered, a reset token will be sent."))
                .andExpect(jsonPath("$.data.email").value("farmer@example.com"));
    }

    @Test
    void testForgotPassword_MissingEmail_ReturnsBadRequest() throws Exception {
        when(passwordResetService.forgotPassword(any(ForgotPasswordRequest.class)))
                .thenThrow(new IllegalArgumentException("Email is required"));

        mockMvc.perform(post("/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        when(passwordResetService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn("Password reset successful. Please login again.");

        mockMvc.perform(post("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"sample-token-123\",\"newPassword\":\"NewStrongPassword123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successful. Please login again."))
                .andExpect(jsonPath("$.data.passwordReset").value(true));
    }

    @Test
    void testResetPassword_InvalidToken_ReturnsBadRequest() throws Exception {
        when(passwordResetService.resetPassword(any(ResetPasswordRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid or expired reset token"));

        mockMvc.perform(post("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"expired-token\",\"newPassword\":\"NewStrongPassword123!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired reset token"));
    }
}
