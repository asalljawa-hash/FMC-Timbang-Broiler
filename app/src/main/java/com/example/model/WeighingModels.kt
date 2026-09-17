package com.example.model

import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class WeighingRow(
  val no: Int,
  val ekor: String = "",
  val berat: String = ""
) {
  val ekorInt: Int
    get() = ekor.trim().toIntOrNull() ?: 0

  val beratDouble: Double
    get() = berat.trim().replace(',', '.').toDoubleOrNull() ?: 0.0

  val isValid: Boolean
    get() = ekorInt > 0 && beratDouble > 0.0
}

data class WeighingSet(
  val setNumber: Int,
  val rows: List<WeighingRow> = defaultRows()
) {
  val totalEkor: Int
    get() = rows.sumOf { it.ekorInt }

  val totalBeratKg: Double
    get() = rows.sumOf { it.beratDouble }

  val bobotRataRata: Double
    get() = if (totalEkor > 0) totalBeratKg / totalEkor else 0.0

  companion object {
    fun defaultRows(): List<WeighingRow> {
      return (1..10).map { WeighingRow(no = it) }
    }
  }
}

data class TotalCalculation(
  val totalEkor: Int,
  val totalBeratKg: Double,
  val bobotRataRata: Double,
  val totalSets: Int,
  val totalTimbangan: Int
)

object WeighingFormatter {
  private val decimalSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
    decimalSeparator = ','
    groupingSeparator = '.'
  }

  fun formatKg(value: Double): String {
    val df = DecimalFormat("#,##0.00", decimalSymbols)
    return "${df.format(value)} kg"
  }

  fun formatKgThreeDecimals(value: Double): String {
    val df = DecimalFormat("#,##0.000", decimalSymbols)
    return "${df.format(value)} kg"
  }

  fun formatTonase(value: Double): String {
    val df = DecimalFormat("#,##0.00", decimalSymbols)
    return df.format(value)
  }

  fun formatAvgKg(value: Double): String {
    val df = DecimalFormat("#,##0.000", decimalSymbols)
    return "${df.format(value)} kg/ekor"
  }

  fun formatEkor(value: Int): String {
    val df = DecimalFormat("#,##0", decimalSymbols)
    return "${df.format(value)} ekor"
  }
}

object WeighingSerializer {
  fun setsToJson(sets: List<WeighingSet>): String {
    val array = JSONArray()
    for (set in sets) {
      val setObj = JSONObject()
      setObj.put("setNumber", set.setNumber)
      val rowsArray = JSONArray()
      for (row in set.rows) {
        if (row.ekor.isNotBlank() || row.berat.isNotBlank()) {
          val rowObj = JSONObject()
          rowObj.put("no", row.no)
          rowObj.put("ekor", row.ekor)
          rowObj.put("berat", row.berat)
          rowsArray.put(rowObj)
        }
      }
      setObj.put("rows", rowsArray)
      array.put(setObj)
    }
    return array.toString()
  }

  fun setsFromJson(json: String): List<WeighingSet> {
    val list = mutableListOf<WeighingSet>()
    try {
      val array = JSONArray(json)
      for (i in 0 until array.length()) {
        val setObj = array.getJSONObject(i)
        val setNumber = setObj.optInt("setNumber", i + 1)
        val rowsArray = setObj.optJSONArray("rows") ?: JSONArray()
        val rowMap = mutableMapOf<Int, Pair<String, String>>()
        for (j in 0 until rowsArray.length()) {
          val rowObj = rowsArray.getJSONObject(j)
          val no = rowObj.optInt("no", j + 1)
          val ekor = rowObj.optString("ekor", "")
          val berat = rowObj.optString("berat", "")
          rowMap[no] = Pair(ekor, berat)
        }
        val rows = (1..10).map { no ->
          val pair = rowMap[no]
          WeighingRow(
            no = no,
            ekor = pair?.first ?: "",
            berat = pair?.second ?: ""
          )
        }
        list.add(WeighingSet(setNumber = setNumber, rows = rows))
      }
    } catch (_: Exception) {
      if (list.isEmpty()) {
        list.add(WeighingSet(1))
      }
    }
    return if (list.isEmpty()) listOf(WeighingSet(1)) else list
  }
}
