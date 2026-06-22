# ISSUE-022 — Mobile: Offline Cache & Sync

**Epic:** Offline  
**Labels:** `android`, `offline`, `room`  
**Depends on:** ISSUE-018, ISSUE-019  
**Blocks:** —

---

## Summary

Wire up the Room entities and DAOs that back the offline cache and pending-answer queue.
Scenario summaries and category data are already cached by ISSUE-018's SSOT repository
pattern, but the actual Room schema is stub-only until this issue. This issue also
implements `PendingAnswer` queuing from ISSUE-019 and the sync mechanism that drains
the queue when network connectivity is restored.

---

## Acceptance Criteria

- [ ] `SafetySpotDatabase` defines the full entity list, migration strategy, and fallback `fallbackToDestructiveMigration()` for dev
- [ ] `ScenarioDao` supports upsert-all (cache) and query-by-id; `CategoryDao` supports upsert-all and get-all
- [ ] `PendingAnswerDao` supports insert, get-all-in-order, and delete-by-id; each row stores: `attemptId`, `taskId`, `selectedOptionIds` (JSON), `queuedAt`
- [ ] `ProfileDao` caches the last fetched `UserProfile` for instant display on Profile screen open
- [ ] `SyncWorker` (WorkManager `CoroutineWorker`) is enqueued with `NetworkType.CONNECTED` constraint; it iterates `PendingAnswer` rows, calls `AttemptApi.submitAnswers`, then `AttemptApi.completeAttempt` for rows with a completion marker, and deletes successfully synced rows
- [ ] `SyncWorker` is enqueued from the repository when an answer is queued offline (ISSUE-019 calls `attemptRepository.queueOffline(...)` which inserts the row **and** enqueues the worker)
- [ ] Room in-memory database is used in all tests; `SyncWorkerTest` uses `TestListenableWorkerBuilder`
- [ ] `./gradlew :app:testDebugUnitTest` passes

---

## Technical Details

### Files to create / modify

```
data/local/SafetySpotDatabase.kt             (replace stub)
data/local/dao/ScenarioDao.kt
data/local/dao/CategoryDao.kt
data/local/dao/PendingAnswerDao.kt
data/local/dao/ProfileDao.kt
data/local/entity/ScenarioEntity.kt
data/local/entity/CategoryEntity.kt
data/local/entity/PendingAnswerEntity.kt
data/local/entity/ProfileEntity.kt
data/local/converter/StringListConverter.kt  (stores List<Long> as JSON)
data/sync/SyncWorker.kt
di/DatabaseModule.kt                          (modify — register new DAOs + WorkManager)
```

### `SafetySpotDatabase`

```kotlin
@Database(
    entities = [
        ScenarioEntity::class,
        CategoryEntity::class,
        PendingAnswerEntity::class,
        ProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class SafetySpotDatabase : RoomDatabase() {
    abstract fun scenarioDao(): ScenarioDao
    abstract fun categoryDao(): CategoryDao
    abstract fun pendingAnswerDao(): PendingAnswerDao
    abstract fun profileDao(): ProfileDao
}
```

### `PendingAnswerEntity`

```kotlin
@Entity(tableName = "pending_answers")
data class PendingAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: Long,
    val taskId: Long,
    val selectedOptionIds: String,   // JSON array via StringListConverter
    val isCompletion: Boolean = false,   // true = this row triggers /complete
    val queuedAt: Long = System.currentTimeMillis()
)
```

### `PendingAnswerDao`

```kotlin
@Dao
interface PendingAnswerDao {
    @Insert
    suspend fun insert(entity: PendingAnswerEntity)

    @Query("SELECT * FROM pending_answers ORDER BY queuedAt ASC")
    suspend fun getAll(): List<PendingAnswerEntity>

    @Query("DELETE FROM pending_answers WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

### `SyncWorker`

```kotlin
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val pendingAnswerDao: PendingAnswerDao,
    private val attemptApi: AttemptApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pending = pendingAnswerDao.getAll()
        for (row in pending) {
            val response = if (row.isCompletion) {
                attemptApi.completeAttempt(row.attemptId)
            } else {
                attemptApi.submitAnswers(row.attemptId, row.toRequest())
            }
            if (response.isSuccessful) pendingAnswerDao.deleteById(row.id)
            else return Result.retry()
        }
        return Result.success()
    }

    companion object {
        fun enqueue(workManager: WorkManager) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .build()
            workManager.enqueueUniqueWork("sync_pending_answers",
                ExistingWorkPolicy.KEEP, request)
        }
    }
}
```

### Test class: `PendingAnswerDaoTest` (`@DataBaseTest` with in-memory Room)

| Test | Verifies |
|------|----------|
| `insert_thenGetAll_returnsInOrder()` | FIFO order by `queuedAt` |
| `deleteById_removesOnlyTargetRow()` | other rows untouched |

### Test class: `SyncWorkerTest` (`TestListenableWorkerBuilder`)

| Test | Verifies |
|------|----------|
| `doWork_allSuccess_deletesAllRows()` | queue drained |
| `doWork_apiFailure_retriesWork()` | `Result.retry()` returned |

---

## Out of Scope

- Conflict resolution for answers submitted while offline and online simultaneously (server is the source of truth; server-side idempotency handles duplicates)
- Periodic background sync (one-time worker on reconnect is sufficient for MVP)
- Image/illustration pre-caching for offline play
