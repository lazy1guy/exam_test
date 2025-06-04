package com.exam.exam_test.controller;

import com.exam.exam_test.entity.User;
import com.exam.exam_test.service.AuthService;
import com.exam.exam_test.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AuthService.class}) // 导入安全配置
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "password", Role.STUDENT);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testRegisterUserAsAdmin() throws Exception {
        when(authService.register(any(), any(), any())).thenReturn(testUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\", \"role\": \"STUDENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    void testDeleteUserAsStudentShouldFail() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void testUpdateUserAsTeacher() throws Exception {
        when(authService.updateUser(any(), any(), any(), any())).thenReturn(testUser);

        mockMvc.perform(put("/api/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"updateduser\", \"password\": \"newpassword\", \"role\": \"STUDENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }
}