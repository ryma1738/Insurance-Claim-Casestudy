package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.vehicle.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
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
    private Set<InsuranceClaim> claims = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Vehicle> vehicles = new HashSet<>();

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
