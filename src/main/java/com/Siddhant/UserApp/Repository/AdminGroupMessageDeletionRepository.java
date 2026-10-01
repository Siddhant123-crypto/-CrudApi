package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.AdminGroupMessageDeletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminGroupMessageDeletionRepository extends JpaRepository<AdminGroupMessageDeletion, UUID> {
    List<AdminGroupMessageDeletion> findByAdminId(UUID adminId);
    boolean existsByMessageIdAndAdminId(UUID messageId, UUID adminId);
}
