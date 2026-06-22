# ISSUE-013 — Mobile: Design System (Theme & Reusable Components)

**Epic:** Foundation  
**Labels:** `android`, `ui`, `design-system`  
**Depends on:** ISSUE-012  
**Blocks:** ISSUE-015, ISSUE-016, ISSUE-017, ISSUE-018, ISSUE-019, ISSUE-020, ISSUE-021

---

## Summary

Replace the scaffolded purple Material 3 starter theme with the SafetySpot brand palette and
build the library of reusable composables that every screen depends on. No backend calls
here — this is pure Compose/UI work that can progress in parallel with networking
infrastructure (ISSUE-014).

---

## Acceptance Criteria

- [ ] `Color.kt` defines the brand palette: `BrandGreen`, `BrandBlue`, `BrandCyan`, `PointsYellow`, category accent colours (Chemie/Werkraum/Sport/Technik/Straßenverkehr), difficulty colours (Easy/Medium/Hard), and the answer-button pair (DangerPink, SafeGreen)
- [ ] `Theme.kt` uses a fixed `lightColorScheme` (primary = BrandGreen, secondary = BrandBlue, tertiary = PointsYellow); dynamic colour disabled by default; dark scheme retained
- [ ] `Type.kt` defines a typography scale with bold display for greetings/titles and medium weight for card labels
- [ ] Every component listed below exists in `ui/components/`, compiles, and has at least one `@Preview`
- [ ] A `formatScore(points: Int): String` util renders scores with German thousands separator (e.g. `2450 → "2.450"`)
- [ ] `./gradlew :app:assembleDebug` passes

---

## Technical Details

### Files to create

```
ui/theme/Color.kt                   (replace)
ui/theme/Theme.kt                   (replace)
ui/theme/Type.kt                    (replace)
ui/components/PillButton.kt
ui/components/SafetySpotBottomBar.kt
ui/components/MetricCard.kt
ui/components/StreakRow.kt
ui/components/ProgressBar.kt        (XP + scenario progress, labelled)
ui/components/CategoryTile.kt
ui/components/ScenarioCard.kt
ui/components/DifficultyChip.kt
ui/components/TagChip.kt
ui/components/PodiumColumn.kt
ui/components/LeaderboardRow.kt
ui/components/SearchBar.kt
ui/components/FilterTabRow.kt
ui/components/GradientBackground.kt
ui/components/SectionHeader.kt
ui/components/LoadingIndicator.kt
ui/components/ErrorState.kt
ui/components/EmptyState.kt
util/Formatters.kt
```

### Brand colour palette

```kotlin
val BrandGreen   = Color(0xFF2FB344)
val BrandBlue    = Color(0xFF1B3A6B)
val BrandCyan    = Color(0xFF4FC3F7)
val PointsYellow = Color(0xFFFFC53D)

// Category accents
val ChemieBlueTint    = Color(0xFF4A90D9)
val WerkraumOrange    = Color(0xFFE8833A)
val SportGreen        = Color(0xFF4CAF50)
val TechnikPurple     = Color(0xFF9C27B0)
val TrafficRed        = Color(0xFFE53935)

// Difficulty
val DifficultyEasy   = BrandGreen
val DifficultyMedium = Color(0xFFFFB300)
val DifficultyHard   = Color(0xFFE53935)

// Answer buttons
val DangerPink  = Color(0xFFFFCDD2)
val SafeGreenBg = Color(0xFFC8E6C9)
```

### Key component signatures

```kotlin
// Solid/outlined pill button (matches Anmelden / Registrieren)
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PillButtonVariant = PillButtonVariant.Solid   // Solid | Outlined | Text
)

// Bottom tab bar; activeDestination drives the green highlight
@Composable
fun SafetySpotBottomBar(
    activeDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit
)

// Level badge card or points card
@Composable
fun MetricCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier
)

// Colour-coded topic block with icon + scenario count
@Composable
fun CategoryTile(
    name: String,
    iconKey: String,
    scenarioCount: Int,
    accentColor: Color,
    onClick: () -> Unit
)

// List card for scenario browse
@Composable
fun ScenarioCard(
    title: String,
    subtitle: String,
    difficulty: Difficulty,   // domain enum
    taskCount: Int,
    isNew: Boolean,
    iconKey: String,
    accentColor: Color,
    onClick: () -> Unit
)

// Gold/silver/bronze podium pillar
@Composable
fun PodiumColumn(rank: Int, name: String, score: Int, avatarKey: String?)

// Leaderboard list row; highlighted when isCurrentUser = true
@Composable
fun LeaderboardRow(rank: Int, name: String, score: Int, isCurrentUser: Boolean)
```

### `formatScore`

```kotlin
fun formatScore(points: Int): String =
    NumberFormat.getNumberInstance(Locale.GERMANY).format(points)
// 2450 → "2.450"
```

---

## Out of Scope

- Real data binding (screens are in ISSUE-016–021)
- Image loading (Coil wired in ISSUE-014)
- `SafetySpotIcon` as adaptive launcher icon (ISSUE-022 polish)
