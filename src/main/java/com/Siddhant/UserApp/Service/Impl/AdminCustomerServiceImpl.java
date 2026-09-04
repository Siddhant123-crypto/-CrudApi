package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.OrderRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.AdminCustomerService;
import com.Siddhant.UserApp.dto.admin.AdminCustomerOrderResponse;
import com.Siddhant.UserApp.dto.admin.AdminCustomerResponse;
import com.Siddhant.UserApp.dto.admin.AdminCustomerStatisticsResponse;
import com.Siddhant.UserApp.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    @Override
    public List<AdminCustomerResponse> getAllCustomers() {return userRepository.findAll().stream().filter(user -> user.getRole() == Role.CUSTOMER && !Boolean.TRUE.equals(user.getIsDelete())).map(this::mapToResponse).toList();}
    private AdminCustomerResponse mapToResponse(User user) {return new AdminCustomerResponse(user.getUserId(), user.getName(), user.getEmail(), user.getMobile(), user.getVillage(), user.getAddress(), user.getPostalCode(), user.getState(), user.getStatus(), user.getIsActive(), user.getProfilePhoto());}
    @Override
    public AdminCustomerResponse getCustomerById(UUID customerId) {User user = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (user.getRole() == null || !user.getRole().name().equals("CUSTOMER")) {throw new RuntimeException("Customer not found");
        }return mapToResponse(user);
    }@Override
    public List<AdminCustomerResponse> getActiveCustomers() {
        return userRepository.findByRoleAndStatusAndIsActiveTrue(
                        Role.CUSTOMER, Status.ACTIVE
                ).stream().map(this::mapToResponse).toList();
    }@Override
    public List<AdminCustomerResponse> getBlockedCustomers() {
        return userRepository.findByRoleAndStatusAndIsActiveFalse(Role.CUSTOMER, Status.INACTIVE
                ).stream().map(this::mapToResponse).toList();
    }
    @Override
    public AdminCustomerResponse blockCustomer(UUID customerId) {
        User user = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (user.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");
        }user.setStatus(Status.INACTIVE);user.setIsActive(false);
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }@Override
    public AdminCustomerResponse unblockCustomer(UUID customerId) {
        User user = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (user.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");
        }user.setStatus(Status.ACTIVE);user.setIsActive(true);
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }@Override
    public AdminCustomerResponse deleteCustomer(UUID customerId) {User user = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (user.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");
        }user.setIsDelete(true);user.setIsActive(false);user.setStatus(Status.INACTIVE);
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }@Override
    public List<AdminCustomerOrderResponse> getCustomerOrderHistory(UUID customerId) {User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");}
        return orderRepository.findByCustomer(customer).stream().map(order -> new AdminCustomerOrderResponse(order.getOrderId(), order.getOrderNumber(), customer.getUserId(), order.getCustomerName(), order.getCustomerMobile(), order.getDeliveryAddress(), order.getTotalAmount(), order.getStatus(), order.getPaymentStatus(), order.getCreatedOn())).toList();
    }@Override
    public AdminCustomerStatisticsResponse getCustomerStatistics(UUID customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");}
        Long totalOrders = orderRepository.countByCustomer(customer);Long pendingOrders = orderRepository.countByCustomerAndStatus(customer, OrderStatus.PENDING);Long confirmedOrders = orderRepository.countByCustomerAndStatus(customer, OrderStatus.CONFIRMED);Long deliveredOrders = orderRepository.countByCustomerAndStatus(customer, OrderStatus.DELIVERED);Long cancelledOrders = orderRepository.countByCustomerAndStatus(customer, OrderStatus.CANCELLED);BigDecimal totalSpent = orderRepository.getTotalAmountByCustomer(customer);
        return new AdminCustomerStatisticsResponse(totalOrders, pendingOrders, confirmedOrders, deliveredOrders, cancelledOrders, totalSpent);
    }@Override
    public List<AdminCustomerResponse> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {throw new RuntimeException("Search keyword cannot be empty");
        }return userRepository.searchCustomers(keyword.trim()).stream().map(this::mapToResponse).toList();
    }@Override
    public List<AdminCustomerResponse> filterCustomers(String state, Status status) {
        List<User> customers = userRepository.filterCustomers(state, status);
        return customers.stream().map(this::mapToResponse).toList();
    }@Override
    public AdminCustomerResponse activateCustomer(UUID customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");}
        customer.setIsActive(true);customer.setStatus(Status.ACTIVE);User savedCustomer = userRepository.save(customer);
        return mapToResponse(savedCustomer);
    }@Override
    public AdminCustomerResponse deactivateCustomer(UUID customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER) {throw new RuntimeException("Customer not found");
        }customer.setIsActive(false);customer.setStatus(Status.INACTIVE);User savedCustomer = userRepository.save(customer);
        return mapToResponse(savedCustomer);
    }
}