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
import spot.safety.ssbackend.dto.progress.ProgressEntryResponse;
import spot.safety.ssbackend.dto.progress.ProgressSummaryResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.progress.ProgressController;
import spot.safety.ssbackend.progress.ProgressService;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgressController.class)
@Import(SecurityConfig.class)
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressService progressService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private SecurityUserDetailsService userDetailsService;

    private SecurityUser studentPrincipal() {
        return new SecurityUser(User.builder()
                .id(10L)
                .username("student")
                .role(Role.STUDENT)
                .school(School.builder().id(1L).name("School").build())
                .active(true)
                .build());
    }

    private SecurityUser teacherPrincipal() {
        return new SecurityUser(User.builder()
                .id(11L)
                .username("teacher")
                .role(Role.TEACHER)
                .school(School.builder().id(1L).name("School").build())
                .active(true)
                .build());
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

        org.mockito.Mockito.when(userDetailsService.loadUserByUsername("teacher"))
                .thenReturn(teacherPrincipal());

        org.mockito.Mockito.when(userDetailsService.loadUserByUsername("student"))
                .thenReturn(studentPrincipal());
    }

    @Test
    void getHistory_returnsEntries() throws Exception {
        when(progressService.getHistory(any())).thenReturn(List.of(
                new ProgressEntryResponse(100L, "Image", "Chemie", TagValue.SAFE, true, Instant.parse("2026-01-02T10:00:00Z"))));

        mockMvc.perform(get("/api/v1/progress").with(user(studentPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageId").value(100));
    }

    @Test
    void getSummary_returnsSummary() throws Exception {
        when(progressService.getSummary(any())).thenReturn(new ProgressSummaryResponse(2, 1, 50.0));

        mockMvc.perform(get("/api/v1/progress/summary").with(user(studentPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTagged").value(2))
                .andExpect(jsonPath("$.accuracyPercent").value(50.0));
    }

    @Test
    void teacherForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/progress").with(user(teacherPrincipal())))
                .andExpect(status().isForbidden());
    }
}
