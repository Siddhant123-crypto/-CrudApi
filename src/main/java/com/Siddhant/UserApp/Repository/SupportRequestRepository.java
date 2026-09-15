package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.SupportRequest;
import com.Siddhant.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, UUID> {
    List<SupportRequest> findByUserOrderByCreatedAtDesc(User user);
}
