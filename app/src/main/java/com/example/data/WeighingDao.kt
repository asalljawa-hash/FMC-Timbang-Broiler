package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeighingDao {
  @Query("SELECT * FROM weighing_records ORDER BY timestamp DESC")
  fun getAllRecords(): Flow<List<WeighingRecord>>

  @Query("SELECT * FROM weighing_records WHERE id = :id LIMIT 1")
  suspend fun getRecordById(id: Long): WeighingRecord?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: WeighingRecord): Long

  @Query("DELETE FROM weighing_records WHERE id = :id")
  suspend fun deleteRecordById(id: Long)

  @Query("DELETE FROM weighing_records")
  suspend fun deleteAllRecords()
}
