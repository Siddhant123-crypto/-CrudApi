package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.dto.admin.AdminCustomerOrderResponse;
import com.Siddhant.UserApp.dto.admin.AdminCustomerResponse;
import com.Siddhant.UserApp.dto.admin.AdminCustomerStatisticsResponse;

import java.util.List;
import java.util.UUID;

public interface AdminCustomerService {
    List<AdminCustomerResponse> getAllCustomers();
    AdminCustomerResponse getCustomerById(UUID customerId);
    List<AdminCustomerResponse> getActiveCustomers();
    List<AdminCustomerResponse> getBlockedCustomers();
    AdminCustomerResponse blockCustomer(UUID customerId);
    AdminCustomerResponse unblockCustomer(UUID customerId);
    AdminCustomerResponse deleteCustomer(UUID customerId);
    List<AdminCustomerOrderResponse> getCustomerOrderHistory(UUID customerId);
    AdminCustomerStatisticsResponse getCustomerStatistics(UUID customerId);
    List<AdminCustomerResponse> searchCustomers(String keyword);
    List<AdminCustomerResponse> filterCustomers(String state, Status status);
    AdminCustomerResponse activateCustomer(UUID customerId);
    AdminCustomerResponse deactivateCustomer(UUID customerId);
}