package com.Siddhant.UserApp.mapper;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.dto.FarmerResponse;
import com.Siddhant.UserApp.dto.ProductResponse;

public class MapperBuild {
    public static FarmerResponse buildFarmerResponse(FarmerProfile farmer) {
        if (farmer == null) {return null;}
        FarmerResponse response = new FarmerResponse();
        response.setFarmerId(farmer.getId());
        
        if (farmer.getUser() != null) {
            User user = farmer.getUser();
            response.setName(user.getName());
            response.setVillage(user.getVillage());
            response.setAddress(user.getAddress());
            response.setPostalCode(user.getPostalCode());
            response.setMobile(user.getMobile());
            response.setEmail(user.getEmail());
            response.setState(user.getState());
            response.setProfilePhoto(user.getProfilePhoto());
            response.setCreatedBy(user.getCreatedBy());
            response.setCreatedOn(user.getCreatedOn());
            response.setUpdatedBy(user.getUpdatedBy());
            response.setUpdatedOn(user.getUpdatedOn());
            response.setIsActive(user.getIsActive());
            response.setIsDelete(user.getIsDelete());
            response.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        }
        
        return response;
    }

    public static ProductResponse buildProductResponse(Product product) {
        if (product == null) {return null;
        }
        ProductResponse response = new ProductResponse();
        // -----------------------------------------------
        // PRODUCT ID
        // -----------------------------------------------
        response.setProductId(product.getProductId());

        // -----------------------------------------------
        // FARMER
        // -----------------------------------------------
        if (product.getFarmer() != null) {
            response.setFarmerId(product.getFarmer().getId());
            if (product.getFarmer().getUser() != null) {
                User user = product.getFarmer().getUser();
                response.setFarmerName(user.getName());
                response.setFarmerAddress(user.getAddress());
                response.setFarmerState(user.getState());
                response.setFarmerVillage(user.getVillage());
            }
        }

        // -----------------------------------------------
        // PRODUCT DETAILS
        // -----------------------------------------------
        response.setProductName(product.getProductName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setUnit(product.getUnit());
        response.setDescription(product.getDescription());

        // -----------------------------------------------
        // IMAGE
        // -----------------------------------------------
        response.setProductPhoto(product.getProductPhoto());

        // -----------------------------------------------
        // OPTIONAL VIDEO
        // -----------------------------------------------
        response.setProductVideo(product.getProductVideo());

        // -----------------------------------------------
        // AUDIT FIELDS
        // -----------------------------------------------
        response.setCreatedBy(product.getCreatedBy());
        response.setCreatedOn(product.getCreatedOn());
        response.setUpdatedBy(product.getUpdatedBy());
        response.setUpdatedOn(product.getUpdatedOn());
        response.setIsActive(product.getIsActive());
        response.setIsDelete(product.getIsDelete());
        response.setStatus(product.getStatus());

        return response;
    }
}