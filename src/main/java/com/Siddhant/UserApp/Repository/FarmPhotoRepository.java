package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.FarmPhoto;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface FarmPhotoRepository extends JpaRepository<FarmPhoto, UUID> {

    List<FarmPhoto> findByFarmer(FarmerProfile farmer);
}