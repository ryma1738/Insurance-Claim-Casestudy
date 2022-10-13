package com.auto.insuranceClaim.dbFile;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class DBFile {
    @Id
    @GeneratedValue
    private Long id;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;

    @JsonIgnore
    @ManyToOne
    private InsuranceClaim claim;

    public DBFile(InsuranceClaim claim, String fileName, String fileType, byte[] data) {
        this.claim = claim;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
