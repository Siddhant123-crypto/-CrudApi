package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.AdminGroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AdminGroupMessageRepository extends JpaRepository<AdminGroupMessage, UUID> {
    List<AdminGroupMessage> findAllByOrderByCreatedAtAsc();
}