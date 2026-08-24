package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Repository.ForgetUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Service.ForgotPasswordService;
import com.Siddhant.UserApp.dto.ForgotPasswordRequest;
@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);
    private final ForgetUserRepository forgetUserRepository;
    @Autowired
    public ForgotPasswordServiceImpl(ForgetUserRepository forgetUserRepository) {
        this.forgetUserRepository = forgetUserRepository;
    }
    @Override
    public String updatePassword(ForgotPasswordRequest request) {
        try {
            log.debug("Attempting to update password for email: {}", request.getEmail());
            User user = forgetUserRepository.findByEmail(request.getEmail());
            if (user == null) {
                return "Email not registered";
            }
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return "Password not match";
            }
            user.setPassword(request.getPassword());
            forgetUserRepository.save(user);
            log.info("Password updated successfully for email: {}", request.getEmail());
            return "Password updated successfully";
        } catch (Exception e) {
            log.error("Error updating password", e);
            return "Error updating password: " + e.getMessage();
        }
    }
}
