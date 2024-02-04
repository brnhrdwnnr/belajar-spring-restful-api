package com.bernhard.restful.controller;

import com.bernhard.restful.entity.User;
import com.bernhard.restful.model.RegisterUserRequest;
import com.bernhard.restful.model.TokenResponse;
import com.bernhard.restful.model.UpdateUserRequest;
import com.bernhard.restful.model.UserResponse;
import com.bernhard.restful.model.WebResponse;
import com.bernhard.restful.repository.UserRepository;
import com.bernhard.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");
        request.setName("Test");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("OK", response.getData());
                }
        );
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        userRepository.save(user);


        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");
        request.setName("Test");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void getUserUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setName("Test");
        user.setToken("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("test", response.getData().getUsername());
            assertEquals("Test", response.getData().getName());
        });
    }

    @Test
    void getUserTokenExpired() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setName("Test");
        user.setToken("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setTokenExpiredAt(System.currentTimeMillis() - 1000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserUnauthorized() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setName("Test");
        user.setToken("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Bernhard");
        request.setPassword("12345");
        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("Bernhard", response.getData().getName());
            assertEquals("test", response.getData().getUsername());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertTrue(BCrypt.checkpw("12345", userDb.getPassword()));
        });
    }
}
