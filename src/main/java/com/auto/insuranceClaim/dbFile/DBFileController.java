package com.auto.insuranceClaim.dbFile;

import com.auto.insuranceClaim.Json.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class DBFileController {
    //Credit for code :https://www.callicoder.com/spring-boot-file-upload-download-jpa-hibernate-mysql-database-example/

    private static final Logger logger = LoggerFactory.getLogger(DBFileController.class);

    @Autowired private BDFileService fileService;

    @PostMapping("/uploadFile/{claimId}")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                         @PathVariable Long claimId) {
        DBFile dbFile = fileService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(dbFile.getId().toString())
                .toUriString();

        return new UploadFileResponse(dbFile.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles/{claimId}")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                        @PathVariable Long claimId) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file, claimId))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Object> downloadFile(@PathVariable Long fileId) {
        // Load file from database
        DBFile dbFile = fileService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));
    }
}
