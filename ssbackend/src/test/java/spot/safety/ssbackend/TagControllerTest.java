package spot.safety.ssbackend;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spot.safety.ssbackend.auth.JwtAuthFilter;
import spot.safety.ssbackend.auth.SecurityConfig;
import spot.safety.ssbackend.auth.SecurityUserDetailsService;
import spot.safety.ssbackend.dto.tag.SubmitTagRequest;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.DuplicateTagException;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.tag.TagController;
import spot.safety.ssbackend.tag.TagService;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@Import(SecurityConfig.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private SecurityUserDetailsService userDetailsService;

    @Test
    void submitTag_asTeacher_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/images/1/tag")
                        .with(user(new SecurityUser(User.builder()
                                .id(1L)
                                .username("teacher")
                                .role(Role.TEACHER)
                                .school(School.builder().id(1L).name("School").build())
                                .active(true)
                                .build())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitTagRequest(TagValue.SAFE))))
                .andExpect(status().isForbidden());
    }

    @Test
    void submitTag_invalidTagValue_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/images/1/tag")
                        .with(user(new SecurityUser(User.builder()
                                .id(2L)
                                .username("student")
                                .role(Role.STUDENT)
                                .school(School.builder().id(1L).name("School").build())
                                .active(true)
                                .build())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tag\":\"NOT_A_TAG\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitTag_duplicate_returns409() throws Exception {
        doThrow(new DuplicateTagException("Image already tagged"))
                .when(tagService).submitTag(any(), any(), any());

        mockMvc.perform(post("/api/v1/images/1/tag")
                        .with(user(new SecurityUser(User.builder()
                                .id(2L)
                                .username("student")
                                .role(Role.STUDENT)
                                .school(School.builder().id(1L).name("School").build())
                                .active(true)
                                .build())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitTagRequest(TagValue.SAFE))))
                .andExpect(status().isConflict());
    }
}
