package com.auto.insuranceClaim.dbFile;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.claim.InsuranceClaimRepository;
import com.auto.insuranceClaim.exceptions.FileStorageException;
import com.auto.insuranceClaim.exceptions.NotFoundException;
import com.auto.insuranceClaim.exceptions.UserDoesntMatchException;
import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.user.UserRepository;
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

    @Autowired private DBFileRepository dbFileRepository;
    @Autowired private InsuranceClaimRepository claimRep;
    @Autowired private UserRepository userRep;

    public DBFile storeFile(MultipartFile file, Long claimId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
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
                return dbFile;
            } else throw new NotFoundException("Insurance Claim");

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }
    }

    public DBFile getFileUser(Long fileId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> confirm = userRep.findByEmail(email);
        if(confirm.isPresent()) {
            DBFile dbFile = dbFileRepository.findById(fileId)
                    .orElseThrow(() -> new NotFoundException("File with id " + fileId));
            if (confirm.get().getId().equals(dbFile.getClaim().getUser().getId())) {
                return dbFile;
            } else throw new UserDoesntMatchException();
        } else throw new NotFoundException("User");
    }

    public DBFile getFile(Long fileId) throws FileNotFoundException {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));
    }

}
