package com.example.data

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    private var dbInstance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return dbInstance ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "math_institute_db"
            )
            .fallbackToDestructiveMigration()
            .build()
            dbInstance = instance
            instance
        }
    }
}
