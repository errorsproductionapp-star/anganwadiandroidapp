package com.example.anganwadiapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.anganwadiapp.data.local.dao.ChildDao
import com.example.anganwadiapp.data.local.entity.ChildEntity

@Database(
    entities = [ChildEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao

    companion object {
        const val DATABASE_NAME = "anganwadi_database"
    }
}
