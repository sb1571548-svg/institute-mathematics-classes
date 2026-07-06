package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Users
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

    // Batches
    @Query("SELECT * FROM batches ORDER BY name ASC")
    fun getAllBatches(): Flow<List<Batch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: Batch): Long

    // Live Classes
    @Query("SELECT * FROM live_classes ORDER BY id DESC")
    fun getAllLiveClasses(): Flow<List<LiveClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveClass(liveClass: LiveClass): Long

    @Delete
    suspend fun deleteLiveClass(liveClass: LiveClass)

    // Study Materials
    @Query("SELECT * FROM study_materials ORDER BY timestamp DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterial): Long

    // Tests
    @Query("SELECT * FROM tests ORDER BY id DESC")
    fun getAllTests(): Flow<List<Test>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: Test): Long

    // Test Submissions
    @Query("SELECT * FROM test_submissions ORDER BY timestamp DESC")
    fun getAllTestSubmissions(): Flow<List<TestSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestSubmission(submission: TestSubmission): Long

    // Homework Submissions
    @Query("SELECT * FROM homework_submissions ORDER BY timestamp DESC")
    fun getAllHomeworkSubmissions(): Flow<List<HomeworkSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeworkSubmission(submission: HomeworkSubmission): Long

    // Attendance
    @Query("SELECT * FROM attendance ORDER BY date DESC, studentName ASC")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    // Fees
    @Query("SELECT * FROM fees ORDER BY dueDate ASC")
    fun getAllFees(): Flow<List<Fee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: Fee): Long

    @Update
    suspend fun updateFee(fee: Fee)

    // Doubts
    @Query("SELECT * FROM doubts ORDER BY timestamp DESC")
    fun getAllDoubts(): Flow<List<Doubt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: Doubt): Long

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification): Long
}
