package com.Siddhant.UserApp.Config;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class DatabaseSchemaFixer {
    @Autowired
    private EntityManager entityManager;
    @PostConstruct
    @Transactional
    public void fixSchema() {
        try {
            log.info("Attempting to fix support_messages and support_requests text columns...");
            entityManager.createNativeQuery("ALTER TABLE support_messages MODIFY COLUMN message TEXT").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE support_requests MODIFY COLUMN description TEXT").executeUpdate();
            log.info("Successfully fixed support_messages and support_requests text columns.");
        } catch (Exception e) {
            log.warn("Could not alter tables automatically, they might already be fixed or dialect does not support it: " + e.getMessage());
        }
    }
}
