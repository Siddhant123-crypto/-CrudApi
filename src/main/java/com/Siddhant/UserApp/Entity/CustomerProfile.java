package com.Siddhant.UserApp.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CustomerProfile(User user) {
        this.user = user;
    }
}
