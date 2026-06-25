package spot.safety.ssbackend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spot.safety.ssbackend.auth.JwtAuthFilter;
import spot.safety.ssbackend.auth.SecurityConfig;
import spot.safety.ssbackend.auth.SecurityUserDetailsService;
import spot.safety.ssbackend.dto.school.CreateSchool;
import spot.safety.ssbackend.dto.school.UpdateSchoolRequest;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClassService;
import spot.safety.ssbackend.school.SchoolController;
import spot.safety.ssbackend.school.SchoolService;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchoolController.class)
@Import(SecurityConfig.class)
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SchoolService schoolService;

    @MockitoBean
    private SchoolClassService classGroupService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private SecurityUserDetailsService userDetailsService;

    private SecurityUser adminPrincipal() {
        return new SecurityUser(User.builder()
                .id(1L).username("admin").role(Role.ADMIN).active(true).build());
    }

    private SecurityUser teacherPrincipal() {
        return new SecurityUser(User.builder()
                .id(2L).username("teacher").role(Role.TEACHER).active(true).build());
    }

    private SecurityUser studentPrincipal() {
        return new SecurityUser(User.builder()
                .id(3L).username("student").role(Role.STUDENT).active(true).build());
    }

    @BeforeEach
    void setUp() throws Exception {
        // Since filters are now enabled, the mocked JwtAuthFilter must be told
        // to pass the request along the chain instead of swallowing/blocking it.
        org.mockito.Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    private School sampleSchool() {
        return School.builder()
                .id(1L).name("Sample School").licenseStatus(LicenseStatus.ACTIVE).build();
    }

    @Test
    void getAllSchools_asAdmin_returnsList() throws Exception {
        when(schoolService.getAllSchools()).thenReturn(List.of(sampleSchool()));

        mockMvc.perform(get("/api/v1/schools").with(user(adminPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sample School"));
    }

    @Test
    void getAllSchools_asTeacher_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/schools").with(user(teacherPrincipal())))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllSchools_asStudent_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/schools").with(user(studentPrincipal())))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSchoolById_asAdmin_returnsSchool() throws Exception {
        when(schoolService.getSchoolById(1)).thenReturn(sampleSchool());

        mockMvc.perform(get("/api/v1/schools/1").with(user(adminPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sample School"));
    }

    @Test
    void getSchoolById_asStudent_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/schools/1").with(user(studentPrincipal())))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateSchool_asAdmin_returnsOk() throws Exception {
        String body = """
                {"name":"Updated Name","licenseKey":"KEY-123"}
                """;

        mockMvc.perform(put("/api/v1/schools/1")
                        .with(user(adminPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("All good"));

        verify(schoolService).updateSchool(eq(1L), any(UpdateSchoolRequest.class));
    }

    @Test
    void updateSchool_asTeacher_isForbidden() throws Exception {
        String body = """
                {"name":"Updated Name","licenseKey":"KEY-123"}
                """;

        mockMvc.perform(put("/api/v1/schools/1")
                        .with(user(teacherPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void activateLicense_asAdmin_returnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/schools/1/activate-license")
                        .with(user(adminPrincipal())).with(csrf())
                        .contentType("application/json").content("KEY-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("All good"));

        verify(schoolService).activateLicense(eq(1L), eq("KEY-123"), any());
    }

    @Test
    void activateLicense_asTeacher_returnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/schools/1/activate-license")
                        .with(user(teacherPrincipal())).with(csrf())
                        .contentType("application/json").content("\"KEY-123\""))
                .andExpect(status().isOk());
    }

    @Test
    void activateLicense_asStudent_isForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/schools/1/activate-license")
                        .with(user(studentPrincipal())).with(csrf())
                        .contentType("application/json").content("\"KEY-123\""))
                .andExpect(status().isForbidden());
    }

    @Test
    void newSchool_asAdmin_returnsCreatedSchool() throws Exception {
        when(schoolService.createSchool(any(CreateSchool.class))).thenReturn(sampleSchool());

        String body = """
                {"name":"New School","licenseKey":"KEY-456"}
                """;

        mockMvc.perform(post("/api/v1/schools")
                        .with(user(adminPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sample School"));
    }

    @Test
    void newSchool_asTeacher_isForbidden() throws Exception {
        String body = """
                {"name":"New School","licenseKey":"KEY-456"}
                """;

        mockMvc.perform(post("/api/v1/schools")
                        .with(user(teacherPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isForbidden());
    }
}