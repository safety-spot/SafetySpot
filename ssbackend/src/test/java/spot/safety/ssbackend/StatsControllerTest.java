package spot.safety.ssbackend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spot.safety.ssbackend.auth.JwtAuthFilter;
import spot.safety.ssbackend.auth.SecurityConfig;
import spot.safety.ssbackend.auth.SecurityUserDetailsService;
import spot.safety.ssbackend.dto.stats.ClassStatsResponse;
import spot.safety.ssbackend.dto.stats.ImageStatsResponse;
import spot.safety.ssbackend.dto.stats.StudentStatEntry;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.stats.StatsController;
import spot.safety.ssbackend.stats.StatsService;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatsService statsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private SecurityUserDetailsService userDetailsService;

    private SecurityUser teacherPrincipal() {
        return new SecurityUser(User.builder()
                .id(10L)
                .username("teacher")
                .role(Role.TEACHER)
                .school(School.builder().id(1L).name("School").build())
                .active(true)
                .build());
    }

    private SecurityUser adminPrincipal() {
        return new SecurityUser(User.builder()
                .id(11L)
                .username("admin")
                .role(Role.ADMIN)
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

        org.mockito.Mockito.when(userDetailsService.loadUserByUsername("admin"))
                .thenReturn(adminPrincipal());
    }

    @Test
    void classStats_returnsOverview() throws Exception {
        when(statsService.getClassStats(any(), any())).thenReturn(new ClassStatsResponse(
                20L, "8A", 5, 2, List.of(new StudentStatEntry(30L, "anna", 3, 2, 66.666))));

        mockMvc.perform(get("/api/v1/stats/class/20").with(user(teacherPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classId").value(20))
                .andExpect(jsonPath("$.students[0].username").value("anna"));
    }

    @Test
    void classStats_wrongClass_returns403() throws Exception {
        when(statsService.getClassStats(any(), any())).thenThrow(new AccessDeniedException("nope"));

        mockMvc.perform(get("/api/v1/stats/class/99").with(user(teacherPrincipal())))
                .andExpect(status().isForbidden());
    }

    @Test
    void imageStats_returnsDistribution() throws Exception {
        when(statsService.getImageStats(any(), any())).thenReturn(new ImageStatsResponse(
                100L, "Image", TagValue.DANGEROUS, 3, 2, 0.666666, 2, 1));

        mockMvc.perform(get("/api/v1/stats/image/100").with(user(adminPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageId").value(100))
                .andExpect(jsonPath("$.correctResponses").value(2));
    }
}
