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
    List<SupportRequest> findByAssignedAdminIdOrderByUpdatedAtDesc(UUID assignedAdminId);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(sr) FROM SupportRequest sr WHERE sr.assignedAdminId = :adminId AND sr.hasUnreadTransfer = true")
    long countUnreadTransfersByAdminId(@org.springframework.data.repository.query.Param("adminId") UUID adminId);
    List<SupportRequest> findByAssignedAdminIdIsNullAndStatusNotOrderByUpdatedAtDesc(com.Siddhant.UserApp.enums.SupportRequestStatus status);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(sr) FROM SupportRequest sr WHERE sr.assignedAdminId IS NULL AND sr.status != com.Siddhant.UserApp.enums.SupportRequestStatus.RESOLVED")
    long countUnassignedActiveRequests();
    List<SupportRequest> findByStatusOrderByUpdatedAtDesc(com.Siddhant.UserApp.enums.SupportRequestStatus status);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE SupportRequest sr SET sr.assignedAdminId = :adminId, sr.status = :status WHERE sr.supportRequestId = :requestId AND sr.assignedAdminId IS NULL")
    int claimSupportRequest(UUID requestId, UUID adminId, com.Siddhant.UserApp.enums.SupportRequestStatus status);
}
