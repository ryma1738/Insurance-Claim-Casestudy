package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.vehicle.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(unique = true)
    private String email;

    @JsonIgnore
    @NonNull
    private String password;

    private String phoneNumber;

    private Date dob;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<InsuranceClaim> claims;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Vehicle> vehicles;

    @JsonIgnore
    @Column(updatable = false)
    private String role = "user";

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
