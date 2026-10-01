package com.Siddhant.UserApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

public class DbDump {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(UserAppApplication.class, args);
        JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
        
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT user_id, email, role FROM _user WHERE role = 'FARMER' LIMIT 5");
        System.out.println("====== USERS ======");
        for (Map<String, Object> u : users) {
            System.out.println(u);
        }
        
        List<Map<String, Object>> reqs = jdbcTemplate.queryForList("SELECT support_request_id, user_id, status, assigned_admin_id FROM support_requests ORDER BY created_at DESC LIMIT 5");
        System.out.println("====== REQUESTS ======");
        for (Map<String, Object> r : reqs) {
            System.out.println(r);
        }
        
        List<Map<String, Object>> msgs = jdbcTemplate.queryForList("SELECT support_message_id, support_request_id, sender_id, sender_type, message, is_read, created_at FROM support_messages ORDER BY created_at DESC LIMIT 10");
        System.out.println("====== MESSAGES ======");
        for (Map<String, Object> m : msgs) {
            System.out.println(m);
        }
        
        System.exit(0);
    }
}
