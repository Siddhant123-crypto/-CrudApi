package com.Siddhant.UserApp.Config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class EnvValidationConfigTest {

    @Test
    public void testAllPropertiesPresent() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("razorpay.key.id", "rzp_test_sample");
        env.setProperty("razorpay.key.secret", "sample_secret");
        env.setProperty("spring.datasource.password", "db_pass");
        env.setProperty("jwt.secret", "jwt_secret_value");

        EnvValidationConfig config = new EnvValidationConfig(env);
        assertDoesNotThrow(config::validateRequiredEnvProperties);
    }

    @Test
    public void testMissingRazorpayKeyId() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("razorpay.key.id", "");
        env.setProperty("razorpay.key.secret", "sample_secret");
        env.setProperty("spring.datasource.password", "db_pass");
        env.setProperty("jwt.secret", "jwt_secret_value");

        EnvValidationConfig config = new EnvValidationConfig(env);
        assertThatThrownBy(config::validateRequiredEnvProperties)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing value for RAZORPAY_KEY_ID in the backend .env file");
    }

    @Test
    public void testMissingRazorpayKeySecret() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("razorpay.key.id", "rzp_test_sample");
        env.setProperty("razorpay.key.secret", "   ");
        env.setProperty("spring.datasource.password", "db_pass");
        env.setProperty("jwt.secret", "jwt_secret_value");

        EnvValidationConfig config = new EnvValidationConfig(env);
        assertThatThrownBy(config::validateRequiredEnvProperties)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing value for RAZORPAY_KEY_SECRET in the backend .env file");
    }

    @Test
    public void testMissingDbPassword() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("razorpay.key.id", "rzp_test_sample");
        env.setProperty("razorpay.key.secret", "sample_secret");
        env.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/userapp");
        env.setProperty("spring.datasource.password", "");
        env.setProperty("jwt.secret", "jwt_secret_value");

        EnvValidationConfig config = new EnvValidationConfig(env);
        assertThatThrownBy(config::validateRequiredEnvProperties)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing value for DB_PASSWORD in the backend .env file");
    }

    @Test
    public void testMissingJwtSecret() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("razorpay.key.id", "rzp_test_sample");
        env.setProperty("razorpay.key.secret", "sample_secret");
        env.setProperty("spring.datasource.password", "db_pass");
        env.setProperty("jwt.secret", "");

        EnvValidationConfig config = new EnvValidationConfig(env);
        assertThatThrownBy(config::validateRequiredEnvProperties)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing value for JWT_SECRET in the backend .env file");
    }
}
