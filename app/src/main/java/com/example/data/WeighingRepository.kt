package com.example.data

import kotlinx.coroutines.flow.Flow

class WeighingRepository(private val dao: WeighingDao) {
  val allRecords: Flow<List<WeighingRecord>> = dao.getAllRecords()

  suspend fun insert(record: WeighingRecord): Long = dao.insertRecord(record)

  suspend fun delete(id: Long) = dao.deleteRecordById(id)

  suspend fun deleteAll() = dao.deleteAllRecords()
}
