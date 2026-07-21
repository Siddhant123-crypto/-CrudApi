package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, UUID> {
    Optional<FarmerProfile> findByEmail(String email);

    Optional<FarmerProfile> findByMobile(String mobile);

    Optional<FarmerProfile> findByEmailAndPassword(String email, String password);
}
