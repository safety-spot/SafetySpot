# ISSUE-002 — JWT Auth Infrastructure

**Epic:** Auth  
**Labels:** `backend`, `auth`, `security`  
**Depends on:** ISSUE-001  
**Blocks:** ISSUE-003, ISSUE-005, ISSUE-006, ISSUE-008, ISSUE-009

---

## Summary

Implement the JWT plumbing: token generation/validation, the request filter that reads
`Authorization: Bearer <token>` and populates the `SecurityContext`, and the `UserPrincipal`
that carries identity through the app. This is pure infrastructure — no HTTP endpoints are
exposed here, but every authenticated endpoint depends on it.

---

## Acceptance Criteria

- [ ] `JwtTokenProvider` can generate a signed access token and a refresh token from a `User`
- [ ] `JwtTokenProvider` can parse and validate a token, returning claims or throwing on expiry/tampering
- [ ] `JwtAuthenticationFilter` sets `SecurityContextHolder` correctly for valid tokens and does nothing (no exception) for missing/invalid tokens — downstream `@PreAuthorize` handles the 403
- [ ] `SecurityConfig` marks `POST /api/v1/auth/**` as public and requires authentication everywhere else
- [ ] `UserPrincipal` implements `UserDetails`; carries `id`, `role`, `schoolId`, `classId`
- [ ] `UserDetailsServiceImpl` loads a `User` by username from `UserRepository` and wraps it in `UserPrincipal`
- [ ] Unit test: valid token → correct claims; expired token → exception; tampered token → exception

---

## Technical Details

### Files to create

```
security/JwtTokenProvider.java
security/JwtAuthenticationFilter.java        (extends OncePerRequestFilter)
security/UserPrincipal.java
security/UserDetailsServiceImpl.java
config/SecurityConfig.java                   (@Configuration @EnableWebSecurity)
config/JwtConfig.java                        (@ConfigurationProperties("jwt"))
```

### `JwtConfig`

```java
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    private String secret;
    private long accessTokenExpiry;   // seconds
    private long refreshTokenExpiry;  // seconds
}
```

### `JwtTokenProvider` interface + key methods

```java
public class JwtTokenProvider {
    String generateAccessToken(UserPrincipal principal);
    String generateRefreshToken(UserPrincipal principal);
    Claims parseToken(String token);          // throws JwtException on invalid
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
    String getRoleFromToken(String token);
}
```

Token payload (access):
```json
{
  "sub":      "42",
  "role":     "STUDENT",
  "schoolId": "1",
  "classId":  "3",
  "exp":      1234567890
}
```

### `JwtAuthenticationFilter`

```java
@Override
protected void doFilterInternal(HttpServletRequest request, ...) {
    String token = extractBearerToken(request);   // null if absent
    if (token != null && jwtTokenProvider.validateToken(token)) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        UserDetails user = userDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    filterChain.doFilter(request, response);
}
```

### `SecurityConfig` key settings

```java
http
    .csrf(AbstractHttpConfigurer::disable)
    .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/auth/**").permitAll()
        .anyRequest().authenticated()
    )
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

### `UserPrincipal`

```java
@Value   // Lombok immutable
public class UserPrincipal implements UserDetails {
    Long id;
    String username;
    String password;          // hashed — never exposed
    Role role;
    Long schoolId;
    Long classId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    // isAccountNonExpired, isEnabled etc. all return true
}
```

### Dependencies to add to `pom.xml`

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### Test class: `JwtTokenProviderTest`

- `generateAndParse_validToken_returnsCorrectClaims()`
- `validateToken_expiredToken_returnsFalse()`
- `validateToken_tamperedToken_returnsFalse()`

---

## Out of Scope

- Login endpoint (ISSUE-003)
- Refresh token storage/DB (for MVP, refresh tokens are validated by signature only — no DB store)
