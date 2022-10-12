package com.auto.insuranceClaim.dbFile;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

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

    public DBFile(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
