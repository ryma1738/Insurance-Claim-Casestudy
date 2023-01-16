package com.auto.insuranceClaim.Json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBFileJson {
    private Long id;

    private String fileName;

    private String fileType;

}
