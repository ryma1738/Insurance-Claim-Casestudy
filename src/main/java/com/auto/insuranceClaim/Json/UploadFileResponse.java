package com.auto.insuranceClaim.Json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponse {

    private Long id;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
