package com.prjratingsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.GameObjectDTO;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.Rating;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.GameObjectRepository;
import com.prjratingsystem.repository.RatingRepository;
import com.prjratingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RatingSystemIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private User sellerUser;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        commentRepository.deleteAll();
        gameObjectRepository.deleteAll();
        userRepository.deleteAll();

        User adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(Role.ADMIN);
        adminUser.setApproved(true);
        userRepository.save(adminUser);

        sellerUser = new User();
        sellerUser.setEmail("seller@test.com");
        sellerUser.setPassword(passwordEncoder.encode("password"));
        sellerUser.setFirstName("Seller");
        sellerUser.setLastName("User");
        sellerUser.setRole(Role.SELLER);
        sellerUser.setApproved(true);
        userRepository.save(sellerUser);
    }

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAll();
        commentRepository.deleteAll();
        gameObjectRepository.deleteAll();
        userRepository.deleteAll();

        redisTemplate.keys("test:*").forEach(key -> redisTemplate.delete(key));
    }

    @Test
    void testUserRegistrationAndLogin() throws Exception {
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("email", "newuser@test.com");
        registrationData.put("password", "password123");
        registrationData.put("firstName", "New");
        registrationData.put("lastName", "User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));

        User newUser = userRepository.findByEmail("newuser@test.com").orElse(null);
        assertNotNull(newUser);
        assertFalse(newUser.getApproved());

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "newuser@test.com");
        loginData.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "newuser@test.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testCommentSubmissionAndApproval() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setMessage("This seller has great items!");

        mockMvc.perform(post("/api/comments/sellers/%d".formatted(sellerUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated());

        assertEquals(1, commentRepository.count());
        Comment comment = commentRepository.findAll().getFirst();
        assertFalse(comment.getApproved());

        Integer commentId = comment.getId();
        int ratingValue = 5;

        mockMvc.perform(patch("/api/admin/comments/{commentId}/review", commentId)
                        .param("approved", "true")
                        .param("ratingValue", String.valueOf(ratingValue))
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk());

        Comment approvedComment = commentRepository.findById(commentId).get();
        assertTrue(approvedComment.getApproved());

        Rating rating = ratingRepository.findByCommentId(commentId);
        assertEquals(ratingValue, rating.getRatingValue());
    }

    @Test
    void testGameObjectCreationAndRetrieval() throws Exception {
        GameObjectDTO gameObjectDTO = new GameObjectDTO();
        gameObjectDTO.setTitle("CS:GO Knife");
        gameObjectDTO.setText("Rare knife with special pattern");
        gameObjectDTO.setUserId(sellerUser.getId());

        mockMvc.perform(post("/api/game-objects")
                        .with(user("seller@test.com").roles("SELLER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gameObjectDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));

        assertEquals(1, gameObjectRepository.count());

        mockMvc.perform(get("/api/game-objects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("CS:GO Knife")));
    }

    @Test
    void testPasswordReset() throws Exception {
        Map<String, String> resetRequest = new HashMap<>();
        resetRequest.put("email", sellerUser.getEmail());

        mockMvc.perform(post("/api/auth/forgot_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk());

        String resetCode = "test-reset-code";
        redisTemplate.opsForValue().set("password_reset:%s".formatted(resetCode), sellerUser.getEmail(), 30, TimeUnit.MINUTES);

        mockMvc.perform(get("/api/auth/check_code")
                        .param("code", resetCode))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"valid\":true}"));

        Map<String, String> resetData = new HashMap<>();
        resetData.put("code", resetCode);
        resetData.put("newPassword", "newPassword123");

        mockMvc.perform(post("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetData)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(sellerUser.getId()).get();
        assertTrue(passwordEncoder.matches("newPassword123", updatedUser.getPassword()));
    }
}