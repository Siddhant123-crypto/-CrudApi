package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Entity.Notification;
import com.Siddhant.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    // User notifications
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    long countByUserAndIsReadFalse(User user);
    List<Notification> findByUserAndIsReadFalse(User user);
    // Admin notifications
    List<Notification> findBySenderIdOrderByCreatedAtDesc(UUID senderId);
    List<Notification> findByAdminOrderByCreatedAtDesc(Admin admin);
    List<Notification> findByAdminAndIsReadFalseOrderByCreatedAtDesc(Admin admin);
    long countByAdminAndIsReadFalse(Admin admin);
    List<Notification> findByAdminAndIsReadFalse(Admin admin);
}