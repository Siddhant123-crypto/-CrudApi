package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.market.MarketRequest;
import com.Siddhant.UserApp.dto.market.MarketResponse;
import com.Siddhant.UserApp.dto.market.NearbyMarketResponse;
import java.util.List;
import java.util.UUID;
public interface MarketService {
    MarketResponse createMarket(MarketRequest request);
    MarketResponse updateMarket(UUID id, MarketRequest request);
    MarketResponse getMarketById(UUID id);
    List<MarketResponse> getAllMarkets();
    List<NearbyMarketResponse> getNearbyMarkets(Double latitude, Double longitude);
    MarketResponse updateMarketStatus(UUID id, Boolean isActive);
    void deleteMarket(UUID id);
    List<MarketResponse> searchMarketsByName(String name);
}