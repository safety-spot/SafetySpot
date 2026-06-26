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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spot.safety.ssbackend.auth.JwtAuthFilter;
import spot.safety.ssbackend.auth.SecurityConfig;
import spot.safety.ssbackend.auth.SecurityUserDetailsService;
import spot.safety.ssbackend.dto.image.CreateImageRequest;
import spot.safety.ssbackend.dto.image.ImageResponse;
import spot.safety.ssbackend.dto.image.ImageTagResultResponse;
import spot.safety.ssbackend.dto.image.UpdateImageRequest;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.image.ImageController;
import spot.safety.ssbackend.image.ImageService;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private SecurityUserDetailsService userDetailsService;

    private SecurityUser teacherPrincipal() {
        School school = School.builder().id(1L).name("School").build();
        User user = User.builder()
                .id(10L)
                .username("teacher")
                .role(Role.TEACHER)
                .school(school)
                .active(true)
                .build();
        return new SecurityUser(user);
    }

    private SecurityUser studentPrincipal() {
        School school = School.builder().id(1L).name("School").build();
        User user = User.builder()
                .id(11L)
                .username("student")
                .role(Role.STUDENT)
                .school(school)
                .active(true)
                .build();
        return new SecurityUser(user);
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
    void createImage_returnsCreatedImage() throws Exception {
        when(imageService.createImage(any(), any())).thenReturn(new ImageResponse(
                1L, "title", "desc", "https://example.test/1.png", "Chemie", TagValue.SAFE,
                10L, "teacher", Instant.now(), Instant.now()));

        mockMvc.perform(post("/api/v1/images")
                        .with(user(teacherPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateImageRequest(
                                "title", "desc", "https://example.test/1.png", "Chemie", TagValue.SAFE, "ok", "bad"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getImages_masksCorrectTagForStudent() throws Exception {
        when(imageService.getImages(any(), any())).thenReturn(List.of(new ImageResponse(
                1L, "title", "desc", "https://example.test/1.png", "Chemie", null,
                10L, "teacher", Instant.now(), Instant.now())));

        mockMvc.perform(get("/api/v1/images")
                        .with(user(studentPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correctTag").doesNotExist());
    }

    @Test
    void getImage_returnsSingleImage() throws Exception {
        when(imageService.getImage(eq(1L), any())).thenReturn(new ImageResponse(
                1L, "title", "desc", "https://example.test/1.png", "Chemie", TagValue.DANGEROUS,
                10L, "teacher", Instant.now(), Instant.now()));

        mockMvc.perform(get("/api/v1/images/1")
                        .with(user(teacherPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctTag").value("DANGEROUS"));
    }

    @Test
    void updateImage_returnsUpdatedImage() throws Exception {
        when(imageService.updateImage(any(), any(), any())).thenReturn(new ImageResponse(
                1L, "new", "desc", "https://example.test/1.png", "Chemie", TagValue.SAFE,
                10L, "teacher", Instant.now(), Instant.now()));

        mockMvc.perform(put("/api/v1/images/1")
                        .with(user(teacherPrincipal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateImageRequest(
                                "new", "desc", "https://example.test/1.png", "Chemie", TagValue.SAFE, "ok", "bad"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new"));
    }

    @Test
    void deleteImage_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/images/1")
                        .with(user(teacherPrincipal())))
                .andExpect(status().isNoContent());
    }

    @Test
    void getImageResults_returnsResults() throws Exception {
        when(imageService.getImageResults(any(), any())).thenReturn(List.of(
                new ImageTagResultResponse(11L, "student", TagValue.SAFE, false, Instant.now())));

        mockMvc.perform(get("/api/v1/images/1/results")
                        .with(user(teacherPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("student"));
    }
}
