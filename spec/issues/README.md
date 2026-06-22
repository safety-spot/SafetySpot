# SafetySpot — Backend MVP Issues Index

> These issues cover the backend REST API only. The MVP scope is:
> **images that students tag as DANGEROUS or SAFE**, with teachers managing content and
> viewing results. The full quiz/scenario system from `plan.md` is deferred to post-MVP.

---

## Parallel Work Map

```
ISSUE-001  ←────────────────────────────────── (everyone starts here, do first)
    │
    ├──► ISSUE-002  (JWT infrastructure)  ──────────────────┐
    │                                                        │
    └──► ISSUE-004  (Core entities: School/Class/User) ─────┤
              │                                              │
              ├──► ISSUE-005  (User API)         [Person B] ─┤
              │                                              │
              ├──► ISSUE-006  (School/Class API) [Person C] ─┤
              │                                              │
              └──► ISSUE-007  (Image & ImageTag entities)   ─┤
                        │                                    │
                        ├──► ISSUE-003  (Auth endpoints) ◄──┘
                        │        (needs 002 + 004)
                        │
                        ├──► ISSUE-008  (Image management API)
                        │        (needs 002 + 007)
                        │
                        └──► ISSUE-009  (Image tagging API)
                                 (needs 002 + 007)
                                        │
                                  ISSUE-010  (Progress & Stats API)
                                        │
                                  ISSUE-011  (Admin Shell)
                                   (needs 004 + 005)
```

---

## Issues at a Glance

| # | Title | Epic | Depends on | Parallel-safe with |
|---|-------|------|------------|--------------------|
| [001](ISSUE-001-project-foundation.md) | Project Foundation | Foundation | — | — |
| [002](ISSUE-002-jwt-auth-infrastructure.md) | JWT Auth Infrastructure | Auth | 001 | 004 |
| [003](ISSUE-003-auth-endpoints.md) | Auth Endpoints | Auth | 002, 004 | 005, 006, 007 |
| [004](ISSUE-004-core-entities.md) | Core Entities | Org | 001 | 002 |
| [005](ISSUE-005-user-management-api.md) | User Management API | Org | 002, 004 | 006, 007 |
| [006](ISSUE-006-school-class-management-api.md) | School & Class API | Org | 002, 004 | 005, 007 |
| [007](ISSUE-007-image-imagetag-entities.md) | Image & ImageTag Entities | Content | 004 | 005, 006 |
| [008](ISSUE-008-image-management-api.md) | Image Management API | Content | 002, 007 | 009 |
| [009](ISSUE-009-image-tagging-api.md) | Image Tagging API | Gameplay | 002, 007 | 008 |
| [010](ISSUE-010-progress-and-stats-api.md) | Progress & Stats API | Stats | 009 | 011 |
| [011](ISSUE-011-admin-shell.md) | Admin Shell | Foundation | 004, 005 | 010 |

---

## Suggested Sprint Breakdown (2–3 people)

### Sprint 1 — Foundation + Data Layer

| Issue | Owner | Notes |
|-------|-------|-------|
| ISSUE-001 | Person A | Quick (~2h), unblocks everyone |
| ISSUE-002 | Person A | Start immediately after 001 |
| ISSUE-004 | Person B | Parallel with 002 |
| ISSUE-011 | Person C | After 001; can work on shell while waiting for entities |

### Sprint 2 — API Layer

| Issue | Owner | Notes |
|-------|-------|-------|
| ISSUE-003 | Person A | Needs 002 + 004 |
| ISSUE-005 | Person B | Needs 002 + 004 |
| ISSUE-006 | Person C | Needs 002 + 004; parallel with 005 |
| ISSUE-007 | Person B or C | Purely data, no controller conflicts |

### Sprint 3 — MVP Core

| Issue | Owner | Notes |
|-------|-------|-------|
| ISSUE-008 | Person A or B | Needs 002 + 007 |
| ISSUE-009 | Person C | Needs 002 + 007; parallel with 008 |
| ISSUE-010 | Any | Needs 009; last piece |

---

## MVP Endpoints Summary

When all issues are complete, these endpoints exist:

```
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout

GET    /api/v1/users
POST   /api/v1/users
GET    /api/v1/users/{id}
PUT    /api/v1/users/{id}
DELETE /api/v1/users/{id}
POST   /api/v1/users/{id}/reset-password

GET    /api/v1/schools
POST   /api/v1/schools
GET    /api/v1/schools/{id}
PUT    /api/v1/schools/{id}
POST   /api/v1/schools/{id}/activate-license

GET    /api/v1/classes
POST   /api/v1/classes
GET    /api/v1/classes/{id}
PUT    /api/v1/classes/{id}
DELETE /api/v1/classes/{id}
GET    /api/v1/classes/{id}/students

GET    /api/v1/images
POST   /api/v1/images
GET    /api/v1/images/{id}
PUT    /api/v1/images/{id}
DELETE /api/v1/images/{id}
GET    /api/v1/images/{id}/results
POST   /api/v1/images/{id}/tag

GET    /api/v1/progress
GET    /api/v1/progress/summary
GET    /api/v1/stats/class/{classId}
GET    /api/v1/stats/image/{imageId}
```

---

## Post-MVP (Deferred)

The following items from `plan.md` are explicitly **not** in these issues:

- Full Scenario/Task/Quiz system
- Attempt tracking and multi-step gameplay
- Leaderboard and streak/points system
- AuditLog
- Pagination on list endpoints
- File upload / binary image storage
- Token blacklisting on logout
- Rate limiting
