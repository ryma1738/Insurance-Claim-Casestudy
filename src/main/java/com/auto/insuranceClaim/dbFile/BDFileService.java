package com.auto.insuranceClaim.dbFile;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.claim.InsuranceClaimRepository;
import com.auto.insuranceClaim.exceptions.FileStorageException;
import com.auto.insuranceClaim.exceptions.NotFoundException;
import com.auto.insuranceClaim.exceptions.UserDoesntMatchException;
import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
public class BDFileService {

    Logger logger = LoggerFactory.getLogger(BDFileService.class);

    @Autowired private DBFileRepository dbFileRepository;
    @Autowired private InsuranceClaimRepository claimRep;
    @Autowired private UserRepository userRep;

    public DBFile storeFile(MultipartFile file, Long claimId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.trace("Attempting to save file");
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                logger.error("Filename contains invalid path sequence " + fileName);
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Optional<InsuranceClaim> claimConfirm = claimRep.findById(claimId);
            if (claimConfirm.isPresent()) {
                InsuranceClaim claim = claimConfirm.get();
               DBFile dbFile = dbFileRepository.save(new DBFile(claim, fileName, file.getContentType(), file.getBytes()));
                Set<DBFile> documents = claim.getDocuments();
                documents.add(dbFile);
                claim.setDocuments(documents);
                claimRep.save(claim);
                logger.info("File saved to database successfully");
                return dbFile;
            } else {
                logger.error("Insurance Claim was not found");
                throw new NotFoundException("Insurance Claim");
            }

        } catch (IOException ex) {
            logger.error("Could not store file " + fileName);
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }
    }

    public DBFile getFileUser(Long fileId) {
        logger.trace("User attempting to Download file with id: " + fileId);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> confirm = userRep.findByEmail(email);
        if(confirm.isPresent()) {
            DBFile dbFile = dbFileRepository.findById(fileId)
                    .orElseThrow(() -> {
                        logger.error("File with id: " + fileId + " was not found");
                        return new NotFoundException("File with id " + fileId);
                    });
            if (confirm.get().getId().equals(dbFile.getClaim().getUser().getId())) {
                return dbFile;
            } else {
                logger.warn("User with id: " + confirm.get().getId()
                        + " attempted to download file that did not belong to them!");
                throw new UserDoesntMatchException();
            }
        } else {
            logger.error("User not found");
            throw new NotFoundException("User");
        }
    }

    public DBFile getFile(Long fileId) throws FileNotFoundException {
        logger.trace("Employee attempting to Download file with id: " + fileId);
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> {
                    logger.error("File with id: " + fileId + " was not found");
                    return new FileNotFoundException("File not found with id " + fileId);
                });
    }

}
