package com.auto.insuranceClaim.claims;

import com.auto.insuranceClaim.Json.*;
import com.auto.insuranceClaim.claim.ClaimStatus;
import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.claim.InsuranceClaimRepository;
import com.auto.insuranceClaim.claim.InsuranceClaimService;
import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.user.UserRepository;
import com.auto.insuranceClaim.user.UserService;
import com.auto.insuranceClaim.vehicle.Make;
import com.auto.insuranceClaim.vehicle.UseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InsuranceClaimServiceTest {
    private String employeeJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJjYXNlU3R1ZHkvaGV4YXdhcmUvcnltYTE3MzgiLCJpYXQiOjE2NjY3MDc4NDUsImVtYWlsIjoiZW1wbG95ZWUxQGdtYWlsLmNvbSJ9" +
            ".aQv3nQzbjuKlYH5hnlzOmrm6M-s0D3IfKK5m7jjIR0Y";

    private String userJwt;

    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private InsuranceClaimService claimService;

    @Autowired private InsuranceClaimRepository claimRep;

    @Autowired private UserService userService;

    @Autowired private UserRepository userRep;

    @Autowired private MockMvc mock;

    private static InsuranceClaim claim;
    private static User user;

    @BeforeAll
    void initialize() throws Exception {
        SignUpCredentials signup = new SignUpCredentials();
        signup.setEmail("Tester2@gmail.com");
        signup.setPassword("Password");
        signup.setPhoneNumber("999-999-9999");
        signup.setDob(new Date());
        userJwt = userService.createUser(signup).get("jwtToken");

        VehicleInfoJson vehicle = new VehicleInfoJson();
        vehicle.setMake(Make.Toyota);
        vehicle.setModel("86");
        vehicle.setYear(2017);
        vehicle.setVin("jou738hvofh938ffif7");
        vehicle.setUseCase(UseCase.Pleasure);

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + userJwt);
        HttpHeaders headers = new HttpHeaders(header);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(vehicle);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/v1/user/vehicle").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson).headers(headers);
        mock.perform(request).andReturn();

        user = userRep.findByEmail("Tester2@gmail.com").get();
        Long vehicleId = user.getVehicles().stream().collect(Collectors.toList()).get(0).getId();

        InsuranceClaimCreationJson jsonClaim = new InsuranceClaimCreationJson();
        jsonClaim.setDescription("this is a random description of words. words are a form of communitcation, " +
                "and this is a test to see if my computer is communicating in a good way.");
        jsonClaim.setVehicleId(vehicleId);
        jsonClaim.setUserId(user.getId());
        ObjectWriter ow2 = mapper.writer().withDefaultPrettyPrinter();
        String requestJson2=ow2.writeValueAsString(jsonClaim);
        RequestBuilder request2 = MockMvcRequestBuilders
                .post("/api/v1/claim").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson2).headers(headers);
        MvcResult result2 = mock.perform(request2).andReturn();
        String json = result2.getResponse().getContentAsString();
        InsuranceClaimInfoJson claimInfo = new ObjectMapper().readValue(json, InsuranceClaimInfoJson.class);
        assertEquals(InsuranceClaimInfoJson.class, claimInfo.getClass());
        claim = claimRep.findById(claimInfo.getId()).get();
    }

    @Test
    void getClaimsByStatus() {
        List<InsuranceClaimFullJson> claims = claimService.getClaimsByStatus(ClaimStatus.PROCESSING);
        assertThat(claims).size().isGreaterThan(0);
    }

    @Test
    void getClaimsEmployee() {
        List<InsuranceClaimFullJson> claims = claimService.getClaims();
        assertThat(claims).size().isGreaterThan(0);
    }

    @Test
    void getClaimById() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + employeeJwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/employee/claim/" + claim.getId())
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        InsuranceClaimFullJson claimJson = new ObjectMapper().readValue(json, InsuranceClaimFullJson.class);
        assertEquals(InsuranceClaimFullJson.class, claimJson.getClass());
    }

    @Test
    void updateClaimStatus() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + employeeJwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.put("/api/v1/employee/claim/"
                        + claim.getId() + "/" + ClaimStatus.APPROVED).headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        InsuranceClaimInfoJson claimJson = new ObjectMapper().readValue(json, InsuranceClaimInfoJson.class);
        assertEquals(InsuranceClaimInfoJson.class, claimJson.getClass());
    }

    @Test
    void userGetClaimById() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + userJwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1//claim/" + claim.getId())
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        InsuranceClaimFullJson claimJson = new ObjectMapper().readValue(json, InsuranceClaimFullJson.class);
        assertEquals(InsuranceClaimFullJson.class, claimJson.getClass());
    }

    @Test
    void createClaim() throws Exception {
        Long vehicleId = user.getVehicles().stream().collect(Collectors.toList()).get(0).getId();
        InsuranceClaimCreationJson jsonClaim = new InsuranceClaimCreationJson();
        jsonClaim.setDescription("this is a random description of words. words are a form of communitcation, " +
                "and this is a test to see if my computer is communicating in a good way.");
        jsonClaim.setVehicleId(vehicleId);
        jsonClaim.setUserId(user.getId());

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + userJwt);
        HttpHeaders headers = new HttpHeaders(header);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(jsonClaim);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/claim").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson).headers(headers);
        MvcResult result2 = mock.perform(request).andReturn();
        String json = result2.getResponse().getContentAsString();
        InsuranceClaimInfoJson claimInfo = new ObjectMapper().readValue(json, InsuranceClaimInfoJson.class);
        assertEquals(InsuranceClaimInfoJson.class, claimInfo.getClass());
    }

    @AfterAll
    void cleanUp() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + userJwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/user")
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
    }


}
