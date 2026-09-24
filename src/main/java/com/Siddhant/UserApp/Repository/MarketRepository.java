package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface MarketRepository extends JpaRepository<Market, UUID> {
    List<Market> findByIsActiveTrue();
    List<Market> findByIsActiveTrueAndMarketNameContainingIgnoreCase(String marketName);
}