package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Service.MarketService;
import com.Siddhant.UserApp.dto.market.MarketRequest;
import com.Siddhant.UserApp.dto.market.MarketResponse;
import com.Siddhant.UserApp.dto.market.NearbyMarketResponse;
import com.Siddhant.UserApp.Entity.Market;
import com.Siddhant.UserApp.Repository.MarketRepository;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
@Service
public class MarketServiceImpl implements MarketService {
    private final MarketRepository marketRepository;
    public MarketServiceImpl(MarketRepository marketRepository) {this.marketRepository = marketRepository;}
    @Override
    public MarketResponse createMarket(MarketRequest request) {
        Market market = new Market();
        market.setMarketName(request.getMarketName());
        market.setDescription(request.getDescription());
        market.setAddress(request.getAddress());
        market.setVillage(request.getVillage());
        market.setTaluka(request.getTaluka());
        market.setDistrict(request.getDistrict());
        market.setState(request.getState());
        market.setPincode(request.getPincode());
        market.setLatitude(request.getLatitude());
        market.setLongitude(request.getLongitude());
        if (request.getOpeningTime() != null) {market.setOpeningTime(LocalTime.parse(request.getOpeningTime()));
        }if (request.getClosingTime() != null) {market.setClosingTime(LocalTime.parse(request.getClosingTime()));
        }market.setMarketContactName(request.getMarketContactName());
        market.setContactNumber(request.getContactNumber());
        if (request.getIsActive() != null) {market.setIsActive(request.getIsActive());
        } else {market.setIsActive(true);
        }Market savedMarket = marketRepository.save(market);
        return convertToResponse(savedMarket);
    }@Override
    public MarketResponse updateMarket(UUID id, MarketRequest request) {
        Market market = marketRepository.findById(id).orElseThrow(() -> new RuntimeException("Market not found"));
        market.setMarketName(request.getMarketName());
        market.setDescription(request.getDescription());
        market.setAddress(request.getAddress());
        market.setVillage(request.getVillage());
        market.setTaluka(request.getTaluka());
        market.setDistrict(request.getDistrict());
        market.setState(request.getState());
        market.setPincode(request.getPincode());
        market.setLatitude(request.getLatitude());
        market.setLongitude(request.getLongitude());
        if (request.getOpeningTime() != null) {market.setOpeningTime(LocalTime.parse(request.getOpeningTime()));
        }if (request.getClosingTime() != null) {market.setClosingTime(LocalTime.parse(request.getClosingTime()));
        }market.setMarketContactName(request.getMarketContactName());
        market.setContactNumber(request.getContactNumber());
        if (request.getIsActive() != null) {market.setIsActive(request.getIsActive());
        }Market updatedMarket = marketRepository.save(market);
        return convertToResponse(updatedMarket);
    }@Override
    public MarketResponse getMarketById(UUID id) {
        Market market = marketRepository.findById(id).orElseThrow(() -> new RuntimeException("Market not found"));return convertToResponse(market);
    }@Override
    public List<MarketResponse> getAllMarkets() {
        List<Market> markets = marketRepository.findByIsActiveTrue();
        List<MarketResponse> response = new ArrayList<>();
        for (Market market : markets) {response.add(convertToResponse(market));
        }return response;
    }@Override
    public List<NearbyMarketResponse> getNearbyMarkets(Double latitude, Double longitude) {
        List<Market> markets = marketRepository.findByIsActiveTrue();
        List<NearbyMarketResponse> response = new ArrayList<>();
        for (Market market : markets) {
            double distance = calculateDistance(latitude, longitude, market.getLatitude(), market.getLongitude()
            );NearbyMarketResponse nearby = new NearbyMarketResponse();
            nearby.setId(market.getId());
            nearby.setMarketName(market.getMarketName());
            nearby.setAddress(market.getAddress());
            nearby.setDistrict(market.getDistrict());
            nearby.setState(market.getState());
            nearby.setLatitude(market.getLatitude());
            nearby.setLongitude(market.getLongitude());
            nearby.setDistance(Math.round(distance * 100.0) / 100.0);
            nearby.setDistanceUnit("km");
            nearby.setIsActive(market.getIsActive());
            nearby.setCreatedAt(market.getCreatedAt());
            response.add(nearby);
        }
        response.sort(Comparator.comparing(NearbyMarketResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(NearbyMarketResponse::getDistance, Comparator.nullsLast(Comparator.naturalOrder())));
        return response;
    }@Override
    public MarketResponse updateMarketStatus(UUID id, Boolean isActive) {
        Market market = marketRepository.findById(id).orElseThrow(() -> new RuntimeException("Market not found"));market.setIsActive(isActive);
        Market updatedMarket = marketRepository.save(market);return convertToResponse(updatedMarket);
    }@Override
    public void deleteMarket(UUID id) {
        if (!marketRepository.existsById(id)) {throw new RuntimeException("Market not found");
        }marketRepository.deleteById(id);
    }private MarketResponse convertToResponse(Market market) {
        MarketResponse response = new MarketResponse();
        response.setId(market.getId());
        response.setMarketName(market.getMarketName());
        response.setDescription(market.getDescription());
        response.setAddress(market.getAddress());
        response.setVillage(market.getVillage());
        response.setTaluka(market.getTaluka());
        response.setDistrict(market.getDistrict());
        response.setState(market.getState());
        response.setPincode(market.getPincode());
        response.setLatitude(market.getLatitude());
        response.setLongitude(market.getLongitude());
        response.setOpeningTime(market.getOpeningTime());
        response.setClosingTime(market.getClosingTime());
        response.setMarketContactName(market.getMarketContactName());
        response.setContactNumber(market.getContactNumber());
        response.setIsActive(market.getIsActive());
        response.setCreatedAt(market.getCreatedAt());
        response.setUpdatedAt(market.getUpdatedAt());
        return response;
    }private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        final int EARTH_RADIUS = 6371;
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }@Override
    public List<MarketResponse> searchMarketsByName(String name) {
        List<Market> markets = marketRepository.findByIsActiveTrueAndMarketNameContainingIgnoreCase(name);
        return markets.stream()
                .map(this::convertToResponse)
                .toList();
    }
}