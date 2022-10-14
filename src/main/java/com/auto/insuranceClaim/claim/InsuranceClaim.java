package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.dbFile.DBFile;
import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.vehicle.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InsuranceClaim {

    @Id
    @GeneratedValue
    private Long Id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    @NonNull
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "claim", fetch = FetchType.EAGER)
    private Set<DBFile> documents = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    @NonNull
    private Vehicle vehicle;

    @Column(length = 1500)
    @NonNull
    private String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private ClaimStatus claimStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
