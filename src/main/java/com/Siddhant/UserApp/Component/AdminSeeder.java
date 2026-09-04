package com.Siddhant.UserApp.Component;

import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email:admin@farm2home.com}")
    private String defaultAdminEmail;

    @Value("${admin.default.password:Admin@123}")
    private String defaultAdminPassword;

    @Value("${admin.default.name:System Admin}")
    private String defaultAdminName;

    @Autowired
    public AdminSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!adminRepository.existsByEmail(defaultAdminEmail)) {
            log.info("Creating default admin account...");
            Admin admin = new Admin();
            admin.setEmail(defaultAdminEmail);
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setName(defaultAdminName);
            admin.setIsActive(true);
            admin.setIsDelete(false);
            
            adminRepository.save(admin);
            log.info("Default admin account created successfully.");
        } else {
            log.info("Default admin account already exists.");
        }
    }
}
