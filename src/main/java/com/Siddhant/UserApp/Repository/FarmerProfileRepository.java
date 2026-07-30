package com.Siddhant.UserApp.Repository;
import java.util.List;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, UUID> {
    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    Optional<FarmerProfile> findByEmail(String email);

    Optional<FarmerProfile> findByMobile(String mobile);

    Optional<FarmerProfile> findFirstByEmail(String email);

    Optional<FarmerProfile> findFirstByMobile(String mobile);

    Optional<FarmerProfile> findByEmailAndPassword(String email, String password);

    Optional<FarmerProfile> findFirstByEmailAndPassword(String email, String password);

    List<FarmerProfile> findByStateIgnoreCase(String state);

    List<FarmerProfile> findByStateAndVillage(String state, String village);


}
