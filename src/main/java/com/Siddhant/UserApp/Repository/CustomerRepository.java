package com.Siddhant.UserApp.Repository;

import com.Siddhant.UserApp.Entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerProfile, UUID> {
}
