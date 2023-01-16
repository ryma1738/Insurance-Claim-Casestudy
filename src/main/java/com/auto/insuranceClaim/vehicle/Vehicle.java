package com.auto.insuranceClaim.vehicle;

import com.auto.insuranceClaim.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @NonNull
    private Make make;

    @NonNull
    private String model;

    @NonNull
    private int year;

    @NonNull
    private String vin;

    @NonNull
    private UseCase useCase;

}
