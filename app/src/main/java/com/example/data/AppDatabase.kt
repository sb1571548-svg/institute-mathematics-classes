package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Batch::class,
        LiveClass::class,
        StudyMaterial::class,
        Test::class,
        TestSubmission::class,
        HomeworkSubmission::class,
        Attendance::class,
        Fee::class,
        Doubt::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
