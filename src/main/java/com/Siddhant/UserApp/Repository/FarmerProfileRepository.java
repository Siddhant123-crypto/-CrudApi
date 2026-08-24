package com.Siddhant.UserApp.Repository;
import java.util.List;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, UUID> {
    Optional<FarmerProfile> findByUser(User user);
    
    boolean existsByUser_Email(String email);
    boolean existsByUser_Mobile(String mobile);
    Optional<FarmerProfile> findByUser_Email(String email);
    Optional<FarmerProfile> findByUser_Mobile(String mobile);
    Optional<FarmerProfile> findFirstByUser_Email(String email);
    Optional<FarmerProfile> findFirstByUser_Mobile(String mobile);
    Optional<FarmerProfile> findByUser_EmailAndUser_Password(String email, String password);
    Optional<FarmerProfile> findFirstByUser_EmailAndUser_Password(String email, String password);
    List<FarmerProfile> findByUser_StateIgnoreCase(String state);
    List<FarmerProfile> findByUser_StateAndUser_Village(String state, String village);
}
