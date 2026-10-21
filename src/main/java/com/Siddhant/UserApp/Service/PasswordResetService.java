package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.ForgotPasswordRequest;
import com.Siddhant.UserApp.dto.ResetPasswordRequest;
import com.Siddhant.UserApp.Entity.PasswordResetToken;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.PasswordResetTokenRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    @org.springframework.beans.factory.annotation.Value("${app.reset-password.deep-link-scheme:farm2home://reset-password}")
    private String deepLinkScheme;

    @org.springframework.beans.factory.annotation.Value("${app.reset-password.web-fallback-url:http://172.16.21.145:8080/user/reset-password-page}")
    private String webFallbackUrl;

    @org.springframework.beans.factory.annotation.Value("${app.reset-password.token-validity-minutes:60}")
    private long tokenValidityMinutes;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        if (request.getEmail() == null
                || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        String email = request.getEmail().trim();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return "If the email is registered, a reset token will be sent.";
        }
        for (PasswordResetToken oldToken : tokenRepository.findAll()) {
            if (oldToken.getEmail().equalsIgnoreCase(email)) {
                tokenRepository.delete(oldToken);
            }
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, email, LocalDateTime.now().plusMinutes(tokenValidityMinutes));
        tokenRepository.save(resetToken);

        String appLink = deepLinkScheme + "?token=" + token;
        String webLink = webFallbackUrl + "?token=" + token;

        try {
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper =
                    new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Farm2Home - Password Reset Link");

            String html = "<!DOCTYPE html>"
                    + "<html><body style=\"font-family: Arial, sans-serif; background-color: #f7faf7; margin: 0; padding: 24px;\">"
                    + "<div style=\"max-width: 540px; margin: 0 auto; background: #ffffff; border-radius: 16px; padding: 32px; border: 1px solid #e0e8e0; box-shadow: 0 4px 12px rgba(0,0,0,0.05);\">"
                    + "<div style=\"text-align: center; margin-bottom: 24px;\">"
                    + "<h1 style=\"color: #1B5E20; margin: 0; font-size: 26px;\">🌱 Farm2Home</h1>"
                    + "<p style=\"color: #556857; margin-top: 6px; font-size: 14px;\">Fresh from the farm, straight to your home</p>"
                    + "</div>"
                    + "<h2 style=\"color: #1A2E1C; font-size: 20px; margin-bottom: 12px;\">Reset Your Password</h2>"
                    + "<p style=\"color: #444444; font-size: 14px; line-height: 1.6;\">Hello,</p>"
                    + "<p style=\"color: #444444; font-size: 14px; line-height: 1.6;\">We received a request to reset the password for your Farm2Home account. Click the button below to choose a new password:</p>"
                    + "<div style=\"text-align: center; margin: 30px 0;\">"
                    + "<a href=\"" + webLink + "\" style=\"background-color: #1B5E20; color: #ffffff; padding: 14px 32px; font-size: 15px; font-weight: bold; text-decoration: none; border-radius: 10px; display: inline-block; box-shadow: 0 3px 6px rgba(27,94,32,0.25);\">Reset Password</a>"
                    + "</div>"
                    + "<p style=\"color: #666666; font-size: 13px; line-height: 1.5; margin-top: 20px;\">If you are on your mobile phone with Farm2Home installed, you can also <a href=\"" + appLink + "\" style=\"color: #2D983A; font-weight: bold;\">open directly in Farm2Home App</a>.</p>"
                    + "<div style=\"background: #f1f8f1; border-left: 4px solid #2D983A; padding: 12px; border-radius: 8px; margin-top: 24px;\">"
                    + "<p style=\"margin: 0; font-size: 12.5px; color: #2E5233;\">⏱️ This link is valid for <strong>" + tokenValidityMinutes + " minutes</strong> and can only be used once.</p>"
                    + "</div>"
                    + "<p style=\"color: #888888; font-size: 12px; margin-top: 24px; border-top: 1px solid #eeeeee; padding-top: 16px;\">If you did not request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>"
                    + "</div></body></html>";

            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            SimpleMailMessage fallback = new SimpleMailMessage();
            fallback.setTo(email);
            fallback.setSubject("Farm2Home - Password Reset Link");
            fallback.setText(
                    "Hello,\n\n"
                            + "We received a request to reset your password for Farm2Home.\n\n"
                            + "Click the link below to reset your password:\n"
                            + webLink + "\n\n"
                            + "Or open directly in the mobile app:\n"
                            + appLink + "\n\n"
                            + "This link expires in " + tokenValidityMinutes + " minutes and can be used only once.\n\n"
                            + "If you did not request this, please ignore this email."
            );
            mailSender.send(fallback);
        }
        return "If the email is registered, a reset token will be sent.";
    }@Transactional
    public String resetPassword(ResetPasswordRequest request) {
        if (request.getToken() == null || request.getNewPassword() == null) {
            throw new IllegalArgumentException(
                    "Token and new password are required"
            );
        }String newPassword = request.getNewPassword();
        if (newPassword.trim().isEmpty()
                || newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }Optional<PasswordResetToken> tokenOptional =
                tokenRepository.findById(request.getToken());
        if (tokenOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid or expired reset token");
        }PasswordResetToken resetToken = tokenOptional.get();
        if (!resetToken.getExpiryDateTime().isAfter(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Invalid or expired reset token");
        }Optional<User> userOptional = userRepository.findByEmail(resetToken.getEmail());
        if (userOptional.isEmpty()) {tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("User account not found");
        }User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
        return "Password reset successful. Please login again.";
    }
}
