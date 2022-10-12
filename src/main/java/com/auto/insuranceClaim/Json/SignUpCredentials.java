package com.auto.insuranceClaim.Json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpCredentials {

    private String email;

    private String password;

    private String phoneNumber;

    private Date dob;
}
