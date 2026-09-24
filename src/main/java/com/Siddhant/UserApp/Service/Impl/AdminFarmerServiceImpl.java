package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.*;
import com.Siddhant.UserApp.Repository.FarmGalleryRepository;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Service.AdminFarmerService;
import com.Siddhant.UserApp.Service.NotificationService;
import com.Siddhant.UserApp.dto.admin.AdminFarmGalleryResponse;
import com.Siddhant.UserApp.dto.admin.AdminFarmVerificationResponse;
import com.Siddhant.UserApp.dto.admin.AdminFarmerResponse;
import com.Siddhant.UserApp.dto.admin.AdminFarmerSalesResponse;
import com.Siddhant.UserApp.dto.admin.AdminFarmVerificationListResponse;
import com.Siddhant.UserApp.dto.admin.AdminFarmVerificationDetailResponse;
import com.Siddhant.UserApp.Repository.FarmDetailsRepository;
import com.Siddhant.UserApp.enums.OrderStatus;
import com.Siddhant.UserApp.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AdminFarmerServiceImpl implements AdminFarmerService {
    private final FarmerProfileRepository farmerProfileRepository;
    private final OrderRepository orderRepository;
    private final FarmGalleryRepository farmGalleryRepository;
    private final NotificationService notificationService;
    private final FarmDetailsRepository farmDetailsRepository;
    @Override
    public List<AdminFarmerResponse> getAllFarmers() {return farmerProfileRepository.findAll().stream().filter(farmer -> farmer.getUser() != null && farmer.getUser().getRole() != null && farmer.getUser().getRole().name().equals("FARMER")).map(this::mapToResponse).toList();
    }private AdminFarmerResponse mapToResponse(FarmerProfile farmer) {var user = farmer.getUser();
        return new AdminFarmerResponse(farmer.getId(), user.getName(), user.getEmail(), user.getMobile(), user.getVillage(), user.getAddress(), user.getPostalCode(), user.getState(), user.getStatus(), user.getIsActive(), user.getInactiveReason(), user.getInactiveSince(), farmer.getFarmName(), farmer.getFarmingType(), farmer.getFarmSize(), farmer.getFarmSizeUnit(), farmer.getMainCrops(), farmer.getExperienceYears(), farmer.getSoilType(), farmer.getFarmVideo(), farmer.getAboutFarm(), farmer.getVerified(), farmer.getCertificateFile(), user.getProfilePhoto());
    }@Override
    public AdminFarmerResponse getFarmerById(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() == null || !farmer.getUser().getRole().name().equals("FARMER")) {
            throw new RuntimeException("Farmer not found");
        }return mapToResponse(farmer);
    }@Override
    public List<AdminFarmerResponse> getPendingFarmers() {
        return farmerProfileRepository.findByUser_Status(Status.PENDING).stream().map(this::mapToResponse).toList();
    }@Override
    public List<AdminFarmerResponse> getVerifiedFarmers() {
        return farmerProfileRepository.findByVerifiedTrue().stream().filter(farmer -> farmer.getUser() != null && farmer.getUser().getRole() != null && farmer.getUser().getRole().name().equals("FARMER")).map(this::mapToResponse).toList();
    }@Override
    public List<AdminFarmerResponse> getBlockedFarmers() {
        return farmerProfileRepository.findByUser_Status(Status.INACTIVE).stream()
                .filter(farmer -> {
                    if (farmer.getUser() != null && farmer.getUser().getRole() != null && farmer.getUser().getRole().name().equals("FARMER")) {
                        com.Siddhant.UserApp.Entity.Status oldStatus = farmer.getUser().getStatus();
                        farmer.getUser().checkAndClearExpiredBlock();
                        if (oldStatus != farmer.getUser().getStatus()) {
                            // If status changed, we can save it here or it will just be omitted from this response
                        }
                        return farmer.getUser().getStatus() == Status.INACTIVE;
                    }
                    return false;
                })
                .map(this::mapToResponse).toList();
    }@Override
    public AdminFarmerResponse blockFarmer(UUID farmerId, String reason, Integer durationDays) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {
            throw new RuntimeException("Farmer not found");
        }farmer.getUser().setIsActive(false);
        farmer.getUser().setStatus(Status.INACTIVE);
        farmer.getUser().setBlockReason(reason);
        if (durationDays != null) {
            farmer.getUser().setBlockedUntil(java.time.LocalDateTime.now().plusDays(durationDays));
        } else {
            farmer.getUser().setBlockedUntil(null); // Indefinite block or logic depends on requirements
        }
        farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse unblockFarmer(UUID farmerId) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");
        }farmer.getUser().setIsActive(true);
        farmer.getUser().setStatus(Status.ACTIVE);
        farmer.getUser().setBlockReason(null);
        farmer.getUser().setBlockedUntil(null);
        farmerProfileRepository.save(farmer);return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse verifyFarmer(UUID farmerId) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");
        }farmer.setVerified(true);
        farmer.setVerifiedOn(java.time.LocalDateTime.now());farmer.getUser().setStatus(Status.ACTIVE);farmer.getUser().setIsActive(true);
        farmerProfileRepository.save(farmer);
        notificationService.createNotification(farmer.getUser().getUserId(), "Farm Verified", "Congratulations! Your farm has been verified by the admin.", NotificationType.SYSTEM);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse deleteFarmer(UUID farmerId) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {
            throw new RuntimeException("Farmer not found");
        }farmer.getUser().setIsDelete(true);farmer.getUser().setIsActive(false);
        farmer.getUser().setStatus(Status.INACTIVE);farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public List<AdminFarmerResponse> searchFarmers(String keyword) {
        return farmerProfileRepository.searchFarmers(keyword).stream().map(this::mapToResponse).toList();
    }@Override
    public List<AdminFarmerResponse> filterFarmersByState(String state) {
        return farmerProfileRepository.findByUser_StateIgnoreCase(state).stream().filter(farmer -> farmer.getUser() != null && farmer.getUser().getRole() == Role.FARMER).map(this::mapToResponse).toList();
    }@Override
    public AdminFarmerSalesResponse getFarmerSales(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        List<Order> orders = orderRepository.findDistinctByOrderItemsFarmer_Id(farmerId);long totalOrders = orders.size();
        long deliveredOrders = orders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).count();long cancelledOrders = orders.stream().filter(order -> order.getStatus() == OrderStatus.CANCELLED).count();
        double totalProductsSold = orders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).flatMap(order -> order.getOrderItems().stream()).filter(item -> item.getFarmer() != null && item.getFarmer().getId().equals(farmerId)).mapToDouble(item -> item.getQuantity().doubleValue()).sum();
        BigDecimal totalRevenue = orders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).flatMap(order -> order.getOrderItems().stream()).filter(item -> item.getFarmer() != null && item.getFarmer().getId().equals(farmerId)).map(OrderItem::getSubtotal).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AdminFarmerSalesResponse(farmerId, farmer.getUser().getName(), totalOrders, deliveredOrders, cancelledOrders, totalProductsSold, totalRevenue);
    }@Override
    public AdminFarmerResponse approveFarmer(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");
        }farmer.setVerified(true);farmer.setVerifiedOn(java.time.LocalDateTime.now());
        farmer.getUser().setStatus(Status.ACTIVE);farmer.getUser().setIsActive(true);farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse rejectFarmer(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");
        }farmer.getUser().setStatus(Status.REJECTED);farmer.getUser().setIsActive(false);
        farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse unverifyFarmer(UUID farmerId) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");
        }farmer.setVerified(false);farmer.setVerifiedOn(null);
        farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse activateFarmer(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");}
        farmer.getUser().setIsActive(true);farmer.getUser().setStatus(Status.ACTIVE);
        farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse deactivateFarmer(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");}
        farmer.getUser().setIsActive(false);farmer.getUser().setStatus(Status.INACTIVE);farmerProfileRepository.save(farmer);
        return mapToResponse(farmer);
    }@Override
    public AdminFarmerResponse getFarmDetails(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");}
        return mapToResponse(farmer);
    }@Override
    public List<AdminFarmGalleryResponse> getFarmGallery(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");}
        return farmGalleryRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId).stream().map(gallery -> new AdminFarmGalleryResponse(gallery.getId(), farmerId, gallery.getFilePath(), gallery.getFileType(), gallery.getOriginalFileName(), gallery.getCreatedAt())).toList();
    }@Override
    public AdminFarmerResponse requestAdditionalInfo(UUID farmerId, String message) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");
        }farmer.setVerificationMessage(message);farmer.setVerificationRequestedOn(java.time.LocalDateTime.now());farmerProfileRepository.save(farmer);
        notificationService.createNotification(farmer.getUser().getUserId(), "Farm Verification Update", message, NotificationType.SYSTEM);
        return mapToResponse(farmer);
    }
    @Override
    public AdminFarmVerificationResponse getFarmVerification(UUID farmerId) {
        FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        if (farmer.getUser() == null || farmer.getUser().getRole() != Role.FARMER) {throw new RuntimeException("Farmer not found");}
        return new AdminFarmVerificationResponse(farmer.getId(), farmer.getUser().getName(), farmer.getVerified(), farmer.getVerificationMessage(), farmer.getVerificationRequestedOn());
    }

    @Override
    public List<AdminFarmVerificationListResponse> getVerificationList() {
        return farmDetailsRepository.findAll().stream().map(farm -> {
            String status = "PENDING";
            if (Boolean.TRUE.equals(farm.getVerified())) {
                status = "VERIFIED";
            } else if (farm.getVerificationMessage() != null && !farm.getVerificationMessage().isEmpty()) {
                status = "REJECTED";
            }
            return new AdminFarmVerificationListResponse(
                farm.getFarmer().getId(),
                farm.getFarmerName() != null ? farm.getFarmerName() : farm.getFarmer().getUser().getName(),
                farm.getFarmName(),
                status,
                farm.getVerificationRequestedOn()
            );
        }).toList();
    }

    @Override
    public AdminFarmVerificationDetailResponse getVerificationDetails(UUID farmerId) {
        FarmDetails farm = farmDetailsRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Farm details not found for farmer"));
        
        var user = farm.getFarmer().getUser();
        AdminFarmVerificationDetailResponse response = new AdminFarmVerificationDetailResponse();
        response.setFarmerId(farm.getFarmer().getId());
        response.setName(user.getName());
        response.setMobile(user.getMobile());
        response.setLocation(farm.getLocation());
        response.setEmail(user.getEmail());
        response.setVillage(user.getVillage());
        response.setState(user.getState());
        response.setProfilePhoto(user.getProfilePhoto());
        response.setFarmName(farm.getFarmName());
        response.setTagline(farm.getTagline());
        response.setFarmingType(farm.getFarmingType());
        response.setFarmArea(farm.getFarmArea());
        response.setFarmAreaUnit(farm.getFarmAreaUnit());
        response.setMainCrops(farm.getMainCrops());
        response.setExperienceYears(farm.getYearsExperience());
        response.setFarmingSince(farm.getFarmingSince());
        response.setCropsGrownCount(farm.getCropsGrownCount());
        response.setHappyCustomersCount(farm.getHappyCustomersCount());
        response.setIrrigationSource(farm.getIrrigationSource());
        response.setSoilType(farm.getSoilType());
        response.setAboutFarm(farm.getAboutFarm());
        response.setCertificateFile(farm.getCertificateFile());
        response.setFarmVideo(farm.getFarmVideo());
        response.setVerified(farm.getVerified());
        response.setVerifiedOn(farm.getVerifiedOn());
        response.setVerificationMessage(farm.getVerificationMessage());
        
        return response;
    }

    @Override
    public void approveFarmerVerification(UUID farmerId) {
        FarmDetails farm = farmDetailsRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Farm details not found"));
        
        farm.setVerified(true);
        farm.setVerifiedOn(java.time.LocalDateTime.now());
        farm.setVerificationMessage(null);
        farmDetailsRepository.save(farm);
        
        notificationService.createNotification(
            farm.getFarmer().getUser().getUserId(), 
            "Farm Verified", 
            "Congratulations! Your farm has been verified by the admin.", 
            NotificationType.SYSTEM
        );
    }

    @Override
    public void rejectFarmerVerification(UUID farmerId, String message) {
        FarmDetails farm = farmDetailsRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Farm details not found"));
        
        farm.setVerified(false);
        farm.setVerificationMessage(message);
        farm.setVerifiedOn(null);
        farmDetailsRepository.save(farm);
        
        notificationService.createNotification(
            farm.getFarmer().getUser().getUserId(), 
            "Farm Verification Update", 
            message, 
            NotificationType.SYSTEM
        );
    }
}