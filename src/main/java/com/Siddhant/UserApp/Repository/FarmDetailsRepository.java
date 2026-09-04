package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.FarmDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmDetailsRepository extends JpaRepository<FarmDetails, UUID> {
    Optional<FarmDetails> findByFarmerId(UUID farmerId);
}
