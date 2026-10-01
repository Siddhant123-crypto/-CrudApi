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
import java.util.Arrays;
import java.util.List;
@Component
public class AdminSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin1.password:Admin@123}")
    private String admin1Password;
    @Value("${admin2.password:Admin2@123}")
    private String admin2Password;
    @Value("${admin3.password:Admin3@123}")
    private String admin3Password;
    @Value("${admin4.password:Admin4@123}")
    private String admin4Password;
    @Value("${admin5.password:Admin5@123}")
    private String admin5Password;
    @Autowired
    public AdminSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }private static class AdminDetails {
        String name;
        String email;
        String mobile;
        String password;
        public AdminDetails(String name, String email, String mobile, String password) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.password = password;
        }
    }@Override
    public void run(String... args) throws Exception {
        List<AdminDetails> configuredAdmins = Arrays.asList(
            new AdminDetails("System Admin", "admin@farm2home.com", "9876543210", admin1Password),
            new AdminDetails("Super Admin", "superadmin@farm2home.com", "9876543211", admin2Password),
            new AdminDetails("Support Admin", "support@farm2home.com", "9876543212", admin3Password),
            new AdminDetails("Finance Admin", "finance@farm2home.com", "9876543213", admin4Password),
            new AdminDetails("Marketing Admin", "marketing@farm2home.com", "9876543214", admin5Password)
        );for (AdminDetails adminDetails : configuredAdmins) {
            if (adminDetails.email == null || adminDetails.email.trim().isEmpty()) {
                log.warn("Skipping admin creation: email is empty for name '{}'", adminDetails.name);
                continue;
            }
            if (!adminRepository.existsByEmail(adminDetails.email)) {
                log.info("Creating admin account for email: {}", adminDetails.email);
                Admin admin = new Admin();
                admin.setName(adminDetails.name);
                admin.setEmail(adminDetails.email);
                admin.setMobile(adminDetails.mobile);
                admin.setPassword(passwordEncoder.encode(adminDetails.password));
                admin.setIsActive(true);
                admin.setIsDelete(false);
                adminRepository.save(admin);
                log.info("Admin account '{}' created successfully.", adminDetails.email);
            } else {
                Admin existingAdmin = adminRepository.findByEmail(adminDetails.email).orElse(null);
                if (existingAdmin != null && !existingAdmin.getName().equals(adminDetails.name)) {
                    log.info("Updating admin account name for '{}' from '{}' to '{}'", adminDetails.email, existingAdmin.getName(), adminDetails.name);
                    existingAdmin.setName(adminDetails.name);
                    adminRepository.save(existingAdmin);
                } else {
                    log.info("Admin account '{}' already exists and is up to date. Skipping.", adminDetails.email);
                }
            }
        }
    }
}
