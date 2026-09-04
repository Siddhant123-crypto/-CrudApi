package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.FarmGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FarmGalleryRepository extends JpaRepository<FarmGallery, UUID> {
    List<FarmGallery> findByFarmerIdOrderByCreatedAtDesc(UUID farmerId);
}
