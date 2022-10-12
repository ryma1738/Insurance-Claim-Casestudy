package com.auto.insuranceClaim.vehicle;

import com.auto.insuranceClaim.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    private Make make;

    private String model;

    private int year;

    @JsonIgnore
    private String vin;

    private UseCase useCase;

}
