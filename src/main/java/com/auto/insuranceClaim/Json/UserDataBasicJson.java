package com.auto.insuranceClaim.Json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataBasicJson {

    private Long id;

    private String email;

    private String phoneNumber;

    private Date dob;
}
