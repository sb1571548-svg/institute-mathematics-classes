package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // Admin, Teacher, Student, Parent
    val className: String // e.g. Class 1, Class 5, etc.
)

@Entity(tableName = "batches")
data class Batch(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gradeClass: String, // Class 1 to 8
    val timing: String
)

@Entity(tableName = "live_classes")
data class LiveClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val batchName: String,
    val mentorName: String,
    val scheduledTime: String,
    val isLive: Boolean = false,
    val recordingUrl: String? = null
)

@Entity(tableName = "study_materials")
data class StudyMaterial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // Video, E-Book, PDF Notes, DPP, Homework, Assignment
    val batchName: String,
    val contentUrl: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tests")
data class Test(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // Chapter, Weekly, Monthly, Olympiad, Mock
    val totalQuestions: Int,
    val durationMinutes: Int,
    val questionsJson: String, // Serialized questions list
    val batchName: String
)

@Entity(tableName = "test_submissions")
data class TestSubmission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: Int,
    val testTitle: String,
    val studentName: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "homework_submissions")
data class HomeworkSubmission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materialId: Int,
    val materialTitle: String,
    val studentName: String,
    val status: String, // Submitted, Graded
    val grade: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val date: String, // YYYY-MM-DD
    val status: String, // Present, Absent
    val batchName: String
)

@Entity(tableName = "fees")
data class Fee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val amount: Double,
    val dueDate: String,
    val status: String, // Due, Paid
    val paidDate: String? = null,
    val paymentMethod: String? = null,
    val transactionId: String? = null
)

@Entity(tableName = "doubts")
data class Doubt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val question: String,
    val answer: String? = null,
    val answeredBy: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val type: String, // Homework, Test, Live Class, Fee Due, Announcement
    val timestamp: Long = System.currentTimeMillis()
)
