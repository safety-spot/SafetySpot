# ISSUE-019 — Mobile: Scenario Gameplay Screen

**Epic:** Gameplay  
**Labels:** `android`, `ui`, `gameplay`  
**Depends on:** ISSUE-018  
**Blocks:** —

---

## Summary

Implement the active gameplay loop shown in `docs/v1/SafetySpot_szenario.md`. The student
works through one task at a time: the screen shows the question, a context illustration,
a descriptive statement, and two or more decision buttons. Each answer is submitted to the
backend immediately; the result (correct/incorrect, points earned, feedback text) is
displayed before advancing. When all tasks are done the attempt is completed server-side
and a result summary is shown.

---

## Acceptance Criteria

- [ ] Entering the screen calls `POST /api/v1/attempts` with the `scenarioId` from the nav argument and stores the `attemptId`
- [ ] Top header shows: back arrow, *"N / total"* step counter with a horizontal `ProgressBar`, and a gold-star points tracker updated after each answer
- [ ] Category tag and mascot illustration are shown above the question
- [ ] Context illustration (`Coil` from `task.imageUrl`) and context text are rendered below the question
- [ ] Binary (Gefährlich / Nicht gefährlich) tasks render two large answer buttons using `DangerPink` / `SafeGreenBg` colours; multi-choice tasks render a vertical list of option buttons
- [ ] Submitting an answer calls `POST /api/v1/attempts/{id}/answers`; the selected button is highlighted as correct (green) or incorrect (red); `feedbackText` is shown; an **Weiter** (Next) button advances to the next task
- [ ] After the last task, `POST /api/v1/attempts/{id}/complete` is called; a **ResultSummary** overlay/screen shows total score, correct count, and a **Zurück zu Szenarien** button
- [ ] Back arrow navigates back without completing the attempt (attempt stays in-progress server-side)
- [ ] Offline: answers are queued as `PendingAnswer` in Room and synced when connectivity returns (ISSUE-022 owns the sync; this issue only enqueues)
- [ ] `ScenarioPlayViewModelTest` covers: attempt start, answer submission, feedback display, attempt completion; `ScenarioPlayScreenTest` verifies task rendering and button colours

---

## Technical Details

### Files to create

```
data/remote/api/AttemptApi.kt
data/remote/api/TaskApi.kt
data/remote/dto/AttemptDto.kt
data/remote/dto/TaskDto.kt
data/local/entity/PendingAnswerEntity.kt
data/repository/AttemptRepositoryImpl.kt
domain/repository/AttemptRepository.kt
domain/model/Task.kt
domain/model/Attempt.kt
domain/model/AnswerResult.kt
ui/scenario/ScenarioPlayScreen.kt
ui/scenario/ScenarioPlayViewModel.kt
ui/scenario/ScenarioPlayUiState.kt
```

### `AttemptApi`

```kotlin
interface AttemptApi {
    @POST("attempts")
    suspend fun startAttempt(@Body request: StartAttemptRequest): Response<AttemptDto>

    @POST("attempts/{id}/answers")
    suspend fun submitAnswers(
        @Path("id") attemptId: Long,
        @Body request: SubmitAnswersRequest
    ): Response<AttemptDto>

    @POST("attempts/{id}/complete")
    suspend fun completeAttempt(@Path("id") attemptId: Long): Response<AttemptDto>
}
```

### `ScenarioPlayUiState`

```kotlin
sealed class ScenarioPlayUiState {
    data object Loading : ScenarioPlayUiState()
    data class Question(
        val attemptId: Long,
        val currentIndex: Int,
        val totalTasks: Int,
        val pointsEarned: Int,
        val task: Task,
        val answerResult: AnswerResult?    // null until answered
    ) : ScenarioPlayUiState()
    data class Result(
        val scoreEarned: Int,
        val scoreMax: Int,
        val correctCount: Int
    ) : ScenarioPlayUiState()
    data class Error(val message: String) : ScenarioPlayUiState()
}

data class AnswerResult(
    val selectedOptionId: Long,
    val wasCorrect: Boolean,
    val pointsEarned: Int,
    val feedbackText: String?
)
```

### `ScenarioPlayViewModel`

```kotlin
@HiltViewModel
class ScenarioPlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val attemptRepository: AttemptRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val scenarioId: Long = savedStateHandle["scenarioId"]!!

    val uiState: StateFlow<ScenarioPlayUiState>

    init { startAttempt() }

    fun submitAnswer(taskId: Long, selectedOptionIds: List<Long>) { ... }
    fun advanceToNextTask() { ... }
    fun completeAttempt() { ... }
}
```

### Screen layout for a binary task

```
Scaffold(topBar = { StepHeader(index, total, points) }) {
    Column {
        TagChip(categoryName)
        MascotImage()                // static drawable
        Text(task.prompt, style = MaterialTheme.typography.headlineSmall)
        Text(task.instructions)
        AsyncImage(task.imageUrl, ...)
        Text(task.contextText)

        Spacer(Modifier.weight(1f))

        // Answer buttons
        Row {
            AnswerButton("Gefährlich",      icon = Icons.Close,  color = DangerPink,  ...)
            AnswerButton("Nicht gefährlich", icon = Icons.Check, color = SafeGreenBg, ...)
        }

        // Feedback + Next — visible after answer
        answerResult?.let {
            FeedbackCard(it.wasCorrect, it.feedbackText, it.pointsEarned)
            Button("Weiter") { advanceOrComplete() }
        }
    }
}
```

### Test class: `ScenarioPlayViewModelTest` (coroutines-test + Turbine)

| Test | Verifies |
|------|----------|
| `init_startsAttempt_emitsFirstQuestion()` | state → Loading → Question(index=0) |
| `submitAnswer_correct_updatesPoints()` | `pointsEarned` increases |
| `submitAnswer_shows_feedbackText()` | `answerResult` set |
| `advanceToNextTask_incrementsIndex()` | `currentIndex` = 1 |
| `afterLastTask_completeAttempt_emitsResult()` | state = Result |
| `submitAnswer_offline_queuesPendingAnswer()` | Room row inserted |

### Test class: `ScenarioPlayScreenTest` (Compose UI)

| Test | Verifies |
|------|----------|
| `questionScreen_binaryTask_showsTwoButtons()` | Gefährlich + Nicht gefährlich |
| `afterAnswer_feedbackCardVisible()` | feedback shown |
| `resultScreen_showsScore()` | score displayed |

---

## Out of Scope

- Multi-step animated transitions between tasks
- Audio/video task types (post-MVP, `TaskType` enum reserved)
- Leaderboard update after completion (server handles this automatically)
