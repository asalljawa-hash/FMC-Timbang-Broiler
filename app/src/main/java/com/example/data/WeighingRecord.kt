package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weighing_records")
data class WeighingRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val namaKandang: String,
  val alamatJalan: String,
  val kecamatan: String,
  val kabupaten: String,
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDate: String,
  val formattedTime: String,
  val totalEkor: Int,
  val totalBeratKg: Double,
  val bobotRataRata: Double,
  val setsJson: String
)
