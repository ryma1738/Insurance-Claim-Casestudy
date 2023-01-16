package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.Json.*;
import com.auto.insuranceClaim.vehicle.Make;
import com.auto.insuranceClaim.vehicle.UseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
public class UserServiceTest {

    private String employeeJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJjYXNlU3R1ZHkvaGV4YXdhcmUvcnltYTE3MzgiLCJpYXQiOjE2NjY3MDc4NDUsImVtYWlsIjoiZW1wbG95ZWUxQGdtYWlsLmNvbSJ9" +
            ".aQv3nQzbjuKlYH5hnlzOmrm6M-s0D3IfKK5m7jjIR0Y";

    private String userJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJjYXNlU3R1ZHkvaGV4YXdhcmUvcnltYTE3MzgiLCJpYXQiOjE2NjY3MTExNzksImVtYWlsIjoidGVzdDFAZ21haWwuY29tIn0" +
            ".yu3s1_c5LaGiHm5ZMjYi--6ZwMas1itgRLS-u0rK7SU";

    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired private UserService userService;

    @Autowired UserRepository userRep;

    @Autowired MockMvc mock;

    @Test
    void getRole() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + userJwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/user/role")
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        RoleJson role = new ObjectMapper().readValue(json, RoleJson.class);
        assertEquals(RoleJson.class, role.getClass());
    }

    @Test
    void getUserInfo() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + userJwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/user")
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        UserDataJson userInfo = new ObjectMapper().readValue(json, UserDataJson.class);
        assertEquals(UserDataJson.class, userInfo.getClass());
    }

    @Test
    void getAllUsers() {
        List<UserDataBasicJson> users = userService.getAllUsers();
        assertThat(users).size().isGreaterThan(0);
    }

    @Test
    void createDeleteUser() throws Exception {
        SignUpCredentials signup = new SignUpCredentials();
        signup.setEmail("Tester2@gmail.com");
        signup.setPassword("Password");
        signup.setPhoneNumber("999-999-9999");
        signup.setDob(new Date());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(signup);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/user/create").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        JwtJson jwt = new ObjectMapper().readValue(json, JwtJson.class);
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt.getJwtToken());
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request2 = MockMvcRequestBuilders.get("/api/v1/user")
                .headers(headers);
        MvcResult result2 = mock.perform(request2).andReturn();
        String json2 = result2.getResponse().getContentAsString();
        UserDataJson userInfo = new ObjectMapper().readValue(json2, UserDataJson.class);
        assertEquals(UserDataJson.class, userInfo.getClass());

        RequestBuilder request3 = MockMvcRequestBuilders.delete("/api/v1/user")
                .headers(headers);
        MvcResult result3 = mock.perform(request3).andReturn();
        int json3 = result3.getResponse().getStatus();
        assertEquals(200, json3);
    }

    @Test
    void login() throws Exception {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("test1@gmail.com");
        credentials.setPassword("Password");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(credentials);
        RequestBuilder request = MockMvcRequestBuilders
            .post("/api/v1/user/login").contentType(APPLICATION_JSON_UTF8)
            .content(requestJson);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        JwtJson jwtConfirm = new ObjectMapper().readValue(json, JwtJson.class);
        assertEquals(JwtJson.class, jwtConfirm.getClass());
    }

    @Test
    void addDeleteVehicle() throws Exception {
        SignUpCredentials signup = new SignUpCredentials();
        signup.setEmail("Tester2@gmail.com");
        signup.setPassword("Password");
        signup.setPhoneNumber("999-999-9999");
        signup.setDob(new Date());
        String jwt = userService.createUser(signup).get("jwtToken");


        VehicleInfoJson vehicle = new VehicleInfoJson();
        vehicle.setMake(Make.Toyota);
        vehicle.setModel("86");
        vehicle.setYear(2017);
        vehicle.setVin("jou738hvofh938ffif7");
        vehicle.setUseCase(UseCase.Pleasure);
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(vehicle);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/v1/user/vehicle").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson).headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        int json = result.getResponse().getStatus();
        assertEquals(201, json);

        User user = userRep.findByEmail("Tester2@gmail.com").get();
        Long vehicleId = user.getVehicles().stream().collect(Collectors.toList()).get(0).getId();


        RequestBuilder request2 = MockMvcRequestBuilders
                .delete("/api/v1/user/vehicle/" + vehicleId).headers(headers);
        MvcResult result2 = mock.perform(request2).andReturn();
        int json2 = result2.getResponse().getStatus();
        assertEquals(200, json2);


        RequestBuilder request3 = MockMvcRequestBuilders.delete("/api/v1/user")
                .headers(headers);
        MvcResult result3 = mock.perform(request3).andReturn();
        int json3 = result3.getResponse().getStatus();
        assertEquals(200, json3);
    }

}
