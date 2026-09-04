package com.Siddhant.UserApp.Repository;
import java.util.List;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<FarmerProfile> findByUser_Status(Status status);
    List<FarmerProfile> findByVerifiedTrue();
    @Query("""
SELECT f FROM FarmerProfile f
JOIN f.user u
WHERE u.role = com.Siddhant.UserApp.Entity.Role.FARMER
AND (
LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
OR u.mobile LIKE CONCAT('%', :keyword, '%')
OR LOWER(f.farmName) LIKE LOWER(CONCAT('%', :keyword, '%'))
)
""")
    List<FarmerProfile> searchFarmers(@Param("keyword") String keyword);

}
