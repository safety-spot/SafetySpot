# ISSUE-011 — Admin Shell (Bootstrap Commands)

**Epic:** Foundation  
**Labels:** `backend`, `admin`, `shell`  
**Depends on:** ISSUE-004, ISSUE-005  
**Blocks:** —

---

## Summary

Spring Shell commands for bootstrapping the system: creating the first school, the first
admin user, and basic management tasks that don't yet have a web UI. Useful for local dev
setup and for ops tasks on production without building an admin web frontend.

---

## Acceptance Criteria

- [ ] `create-school` command creates a school and prints its ID
- [ ] `create-admin` command creates an ADMIN user for a given school (hashed password)
- [ ] `activate-license` command sets a school's license status to ACTIVE with an expiry date
- [ ] `list-schools` prints a table of all schools with license status
- [ ] `reset-password` force-resets any user's password (for ops use)
- [ ] All commands print a success or error message — no silent failures
- [ ] Commands are covered by Spring Shell test (`@ShellTest` / `ShellAssert`)

---

## Technical Details

### Files to create

```
shell/AdminShell.java
```

### Commands

#### `create-school`

```
create-school --name "Gesamtschule Muster"
```
Output: `School created: id=1, name="Gesamtschule Muster"`

Implementation: calls `SchoolService.createSchool(...)` or directly uses `SchoolRepository`.

---

#### `create-admin`

```
create-admin --username "admin1" --password "secret123" --school-id 1
```
Output: `Admin user created: id=5, username="admin1", school=1`

Hashes password with `BCryptPasswordEncoder` before saving.

---

#### `activate-license`

```
activate-license --school-id 1 --expiry 2027-06-01
```
Output: `License activated for school 1, expires 2027-06-01`

Parses `expiry` as `LocalDate`. Validates it is in the future.

---

#### `list-schools`

```
list-schools
```

Output (tabular):
```
ID  Name                     License Status  Expiry
--  -------                  --------------  ----------
1   Gesamtschule Muster      ACTIVE          2027-06-01
2   Realschule Beispiel      INACTIVE        -
```

---

#### `reset-password`

```
reset-password --user-id 3 --password "newpass99"
```
Output: `Password reset for user id=3`

---

### `AdminShell` structure

```java
@ShellComponent
public class AdminShell {

    // Injected via constructor
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolService schoolService;

    @ShellMethod(key = "create-school", value = "Create a new school")
    public String createSchool(
        @ShellOption(value = "--name") String name
    ) { ... }

    @ShellMethod(key = "create-admin", value = "Create an admin user for a school")
    public String createAdmin(
        @ShellOption(value = "--username") String username,
        @ShellOption(value = "--password") String password,
        @ShellOption(value = "--school-id") Long schoolId
    ) { ... }

    @ShellMethod(key = "activate-license", value = "Activate license for a school")
    public String activateLicense(
        @ShellOption(value = "--school-id") Long schoolId,
        @ShellOption(value = "--expiry") String expiry    // parsed as LocalDate
    ) { ... }

    @ShellMethod(key = "list-schools", value = "List all schools")
    public String listSchools() { ... }

    @ShellMethod(key = "reset-password", value = "Force-reset a user password")
    public String resetPassword(
        @ShellOption(value = "--user-id") Long userId,
        @ShellOption(value = "--password") String newPassword
    ) { ... }
}
```

### Test class: `AdminShellTest`

Use `spring-shell-starter-test` (`ShellAssert` / `CommandLine`):

```java
@SpringShellTest
class AdminShellTest {

    @Test
    void createSchool_validName_printsId() { ... }

    @Test
    void createAdmin_hashesPassword() {
        // verify stored hash != raw password
    }

    @Test
    void activateLicense_pastDate_printsError() { ... }

    @Test
    void listSchools_noSchools_printsEmptyMessage() { ... }
}
```

---

## Notes

- Shell commands run **in-process** — they use the same Spring context as the app. No
  separate CLI binary needed.
- For production, run: `java -jar ssbackend.jar --spring.shell.interactive.enabled=true`
- Commands should gracefully catch and print exceptions rather than crash the shell session.

---

## Out of Scope

- Web-based admin dashboard
- Audit logging for shell commands (post-MVP)
