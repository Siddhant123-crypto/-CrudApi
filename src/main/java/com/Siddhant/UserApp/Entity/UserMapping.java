package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Entity
@Table(name = "user_mapping")
public class UserMapping extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "pincode")
    private String pincode;
    @Column(name = "mobile_no")
    private String mobileNo;
    @Column(name = "city")
    private String city;
    @Column(name = "state")
    private String state;
}