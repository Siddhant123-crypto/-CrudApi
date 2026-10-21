package com.Siddhant.UserApp.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EnvValidationConfig {

    private final Environment environment;

    public EnvValidationConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateRequiredEnvProperties() {
        validateProperty("razorpay.key.id", "RAZORPAY_KEY_ID");
        validateProperty("razorpay.key.secret", "RAZORPAY_KEY_SECRET");
        validateProperty("spring.datasource.password", "DB_PASSWORD");
        validateProperty("jwt.secret", "JWT_SECRET");
    }

    private void validateProperty(String propertyKey, String envVarName) {
        String value = environment.getProperty(propertyKey);
        if ("spring.datasource.password".equals(propertyKey)) {
            String url = environment.getProperty("spring.datasource.url");
            if (url != null && url.startsWith("jdbc:h2:") && (value == null || value.trim().isEmpty())) {
                return;
            }
        }
        if (value == null || value.trim().isEmpty()) {
            System.err.println("****************************************************************");
            System.err.println("APPLICATION FAILED TO START");
            System.err.println("Missing value for " + envVarName + " in the backend .env file");
            System.err.println("****************************************************************");
            throw new IllegalStateException("Missing value for " + envVarName + " in the backend .env file");
        }
    }
}
