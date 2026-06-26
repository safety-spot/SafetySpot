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
import spot.safety.ssbackend.dto.user.UserResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassController;
import spot.safety.ssbackend.school.SchoolClassService;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchoolClassController.class)
@Import(SecurityConfig.class)
class SchoolClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SchoolClassService schoolClassService;

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

    private SchoolClass sampleClass() {
        return SchoolClass.builder()
                .id(1L)
                .name("Class A")
                .school(School.builder().id(1L).name("Sample School").build())
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    private UserResponse sampleUserResponse() {
        return new UserResponse(5L, "stud1", Role.STUDENT, 1L, 1L, true,
                Instant.parse("2026-01-01T00:00:00Z"), null);
    }

    // POST /api/v1/classes

    @Test
    void newClass_asAdmin_returnsCreatedClass() throws Exception {
        when(schoolClassService.newClass(eq(2L), eq("Class A"), any())).thenReturn(sampleClass());

        String body = """
                {"schoolId":2,"name":"Class A"}
                """;

        mockMvc.perform(post("/api/v1/classes")
                        .with(user(adminPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Class A"));
    }

    @Test
    void newClass_asTeacher_returnsOk() throws Exception {
        when(schoolClassService.newClass(eq(1L), eq("Class A"), any())).thenReturn(sampleClass());

        String body = """
                {"schoolId":1,"name":"Class A"}
                """;

        mockMvc.perform(post("/api/v1/classes")
                        .with(user(teacherPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void newClass_asStudent_isForbidden() throws Exception {
        String body = """
                {"schoolId":1,"name":"Class A"}
                """;

        mockMvc.perform(post("/api/v1/classes")
                        .with(user(studentPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isForbidden());
    }

    // GET /api/v1/classes

    @Test
    void getClasses_asAdmin_returnsList() throws Exception {
        when(schoolClassService.getClasses(any())).thenReturn(List.of(sampleClass()));

        mockMvc.perform(get("/api/v1/classes").with(user(adminPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Class A"));
    }

    @Test
    void getClasses_asStudent_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/classes").with(user(studentPrincipal())))
                .andExpect(status().isForbidden());
    }

    // GET /api/v1/classes/{id}

    @Test
    void getStudentCount_asTeacher_returnsCount() throws Exception {
        when(schoolClassService.getAmountOfStudent(eq(1L), any())).thenReturn(3);

        mockMvc.perform(get("/api/v1/classes/1").with(user(teacherPrincipal())))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void getStudentCount_asStudent_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/classes/1").with(user(studentPrincipal())))
                .andExpect(status().isForbidden());
    }

    // GET /api/v1/classes/{id}/students

    @Test
    void getStudents_asAdmin_returnsList() throws Exception {
        when(schoolClassService.getStudents(eq(1L), any())).thenReturn(List.of(sampleUserResponse()));

        mockMvc.perform(get("/api/v1/classes/1/students").with(user(adminPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("stud1"));
    }

    @Test
    void getStudents_asStudent_isForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/classes/1/students").with(user(studentPrincipal())))
                .andExpect(status().isForbidden());
    }

    // PUT /api/v1/classes/{id}

    @Test
    void updateClass_asTeacher_returnsOk() throws Exception {
        String body = """
                {"name":"Updated Class","teacherId":null}
                """;

        mockMvc.perform(put("/api/v1/classes/1")
                        .with(user(teacherPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("All good"));

        verify(schoolClassService).updateClass(eq(1L), any(), any());
    }

    @Test
    void updateClass_asStudent_isForbidden() throws Exception {
        String body = """
                {"name":"Updated Class","teacherId":null}
                """;

        mockMvc.perform(put("/api/v1/classes/1")
                        .with(user(studentPrincipal())).with(csrf())
                        .contentType("application/json").content(body))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/v1/classes/{id}

    @Test
    void deleteClass_asAdmin_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/classes/1")
                        .with(user(adminPrincipal())).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("All good"));

        verify(schoolClassService).deleteClass(eq(1L), any());
    }

    @Test
    void deleteClass_asTeacher_isForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/classes/1")
                        .with(user(teacherPrincipal())).with(csrf()))
                .andExpect(status().isForbidden());
    }
}