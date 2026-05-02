package com.example.anganwadiapp.data.local.dao

import androidx.room.*
import com.example.anganwadiapp.data.local.entity.ChildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Query("SELECT * FROM children ORDER BY name ASC")
    fun getAllChildren(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE id = :id")
    fun getChildById(id: String): Flow<ChildEntity?>

    @Query("SELECT * FROM children WHERE id = :id")
    suspend fun getChildByIdOnce(id: String): ChildEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildEntity)

    @Update
    suspend fun updateChild(child: ChildEntity)

    @Delete
    suspend fun deleteChild(child: ChildEntity)
}
