package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.dbFile.DBFile;
import com.auto.insuranceClaim.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class InsuranceClaim {

    @Id
    @GeneratedValue
    private Long Id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @JsonIgnore
    @OneToMany
    private Set<DBFile> documents;

    @Column(length = 1500)
    private String description;

    private ClaimStatus claimStatus;
}
