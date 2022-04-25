package com.example.mynetwork.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mynetwork.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Query("SELECT * FROM JobEntity ORDER BY start DESC")
    fun get(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobs: List<JobEntity>)

    @Query(
        """
        UPDATE JobEntity SET
         name = :name, position = :position, start = :start, finish = :finish
         WHERE id = :id
         """
    )
    suspend fun update(id: Long, name: String, position: String, start: Long, finish: Long?)

    suspend fun save(jobEntity: JobEntity) =
        if (jobEntity.id == 0L) insert(jobEntity) else update(
            jobEntity.id,
            jobEntity.name,
            jobEntity.position,
            jobEntity.start,
            jobEntity.finish
        )

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM JobEntity")
    suspend fun removeAll()
}