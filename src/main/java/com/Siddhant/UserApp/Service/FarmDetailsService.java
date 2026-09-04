package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.FarmDetailsRequest;
import com.Siddhant.UserApp.dto.FarmDetailsResponse;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FarmDetailsService {
    FarmDetailsResponse saveOrUpdateFarmDetails(String requestData, MultipartFile farmerPhoto, MultipartFile farmVideo) throws IOException;
    FarmDetailsResponse getFarmDetailsByFarmerId(UUID farmerId);
    FarmDetailsResponse updateFarmDetails(UUID farmerId, String requestData, MultipartFile farmerPhoto, MultipartFile farmVideo) throws IOException;
    void deleteFarmDetails(UUID farmerId) throws IOException;
}
