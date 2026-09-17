package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.WeighingRecord
import com.example.data.WeighingRepository
import com.example.model.TotalCalculation
import com.example.model.WeighingRow
import com.example.model.WeighingSerializer
import com.example.model.WeighingSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeighingViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: WeighingRepository

  val savedRecords: StateFlow<List<WeighingRecord>>

  init {
    val dao = AppDatabase.getDatabase(application).weighingDao()
    repository = WeighingRepository(dao)
    savedRecords = repository.allRecords.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )
  }

  // Active Weighing Session
  private val _sets = MutableStateFlow(listOf(WeighingSet(setNumber = 1)))
  val sets: StateFlow<List<WeighingSet>> = _sets.asStateFlow()

  private val _currentSetIndex = MutableStateFlow(0)
  val currentSetIndex: StateFlow<Int> = _currentSetIndex.asStateFlow()

  // Save Dialog Form State
  private val _showSaveDialog = MutableStateFlow(false)
  val showSaveDialog: StateFlow<Boolean> = _showSaveDialog.asStateFlow()

  private val _namaKandang = MutableStateFlow("")
  val namaKandang: StateFlow<String> = _namaKandang.asStateFlow()

  private val _alamatJalan = MutableStateFlow("")
  val alamatJalan: StateFlow<String> = _alamatJalan.asStateFlow()

  private val _kecamatan = MutableStateFlow("")
  val kecamatan: StateFlow<String> = _kecamatan.asStateFlow()

  private val _kabupaten = MutableStateFlow("")
  val kabupaten: StateFlow<String> = _kabupaten.asStateFlow()

  private val _formError = MutableStateFlow<String?>(null)
  val formError: StateFlow<String?> = _formError.asStateFlow()

  // Detail View State
  private val _selectedRecord = MutableStateFlow<WeighingRecord?>(null)
  val selectedRecord: StateFlow<WeighingRecord?> = _selectedRecord.asStateFlow()

  // UI Event (Snackbar / notifications)
  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

  // Active Set Navigation & Editing
  fun selectSet(index: Int) {
    if (index in _sets.value.indices) {
      _currentSetIndex.value = index
    }
  }

  fun addNewSet() {
    val currentSets = _sets.value.toMutableList()
    val nextSetNumber = currentSets.size + 1
    currentSets.add(WeighingSet(setNumber = nextSetNumber))
    _sets.value = currentSets
    _currentSetIndex.value = currentSets.size - 1
  }

  fun removeSet(index: Int) {
    val currentSets = _sets.value.toMutableList()
    if (currentSets.size > 1 && index in currentSets.indices) {
      currentSets.removeAt(index)
      // Renumber sets
      val renumbered = currentSets.mapIndexed { idx, set ->
        set.copy(setNumber = idx + 1)
      }
      _sets.value = renumbered
      _currentSetIndex.value = (_currentSetIndex.value.coerceAtMost(renumbered.size - 1))
    }
  }

  fun updateRow(rowIndex: Int, ekor: String, berat: String) {
    val currentSets = _sets.value.toMutableList()
    val currentIdx = _currentSetIndex.value
    if (currentIdx !in currentSets.indices) return

    val set = currentSets[currentIdx]
    val rows = set.rows.toMutableList()
    if (rowIndex !in rows.indices) return

    rows[rowIndex] = rows[rowIndex].copy(
      ekor = ekor,
      berat = berat
    )
    currentSets[currentIdx] = set.copy(rows = rows)
    _sets.value = currentSets
  }

  fun fillDefaultEkorForCurrentSet(defaultEkor: String) {
    val currentSets = _sets.value.toMutableList()
    val currentIdx = _currentSetIndex.value
    if (currentIdx !in currentSets.indices) return

    val set = currentSets[currentIdx]
    val rows = set.rows.map { row ->
      row.copy(ekor = defaultEkor)
    }
    currentSets[currentIdx] = set.copy(rows = rows)
    _sets.value = currentSets
  }

  fun clearCurrentSet() {
    val currentSets = _sets.value.toMutableList()
    val currentIdx = _currentSetIndex.value
    if (currentIdx !in currentSets.indices) return

    currentSets[currentIdx] = WeighingSet(setNumber = currentSets[currentIdx].setNumber)
    _sets.value = currentSets
  }

  fun resetWeighingSession() {
    _sets.value = listOf(WeighingSet(setNumber = 1))
    _currentSetIndex.value = 0
    _namaKandang.value = ""
    _alamatJalan.value = ""
    _kecamatan.value = ""
    _kabupaten.value = ""
    _formError.value = null
    _showSaveDialog.value = false
  }

  // Grand Calculation
  fun calculateGrandTotal(): TotalCalculation {
    val allSets = _sets.value
    var totalEkor = 0
    var totalBeratKg = 0.0
    var totalTimbangan = 0

    for (set in allSets) {
      for (row in set.rows) {
        if (row.ekorInt > 0 || row.beratDouble > 0.0) {
          totalTimbangan++
          totalEkor += row.ekorInt
          totalBeratKg += row.beratDouble
        }
      }
    }

    val avg = if (totalEkor > 0) totalBeratKg / totalEkor else 0.0
    return TotalCalculation(
      totalEkor = totalEkor,
      totalBeratKg = totalBeratKg,
      bobotRataRata = avg,
      totalSets = allSets.size,
      totalTimbangan = totalTimbangan
    )
  }

  // Save Dialog Methods
  fun onSaveButtonClicked(): Boolean {
    val grandTotal = calculateGrandTotal()
    if (grandTotal.totalEkor == 0 || grandTotal.totalBeratKg <= 0.0) {
      viewModelScope.launch {
        _toastMessage.emit("Harap isi data timbang terlebih dahulu!")
      }
      return false
    }
    _formError.value = null
    _showSaveDialog.value = true
    return true
  }

  fun dismissSaveDialog() {
    _showSaveDialog.value = false
    _formError.value = null
  }

  fun updateNamaKandang(value: String) {
    _namaKandang.value = value
  }

  fun updateAlamatJalan(value: String) {
    _alamatJalan.value = value
  }

  fun updateKecamatan(value: String) {
    _kecamatan.value = value
  }

  fun updateKabupaten(value: String) {
    _kabupaten.value = value
  }

  fun submitSave(onSavedSuccessfully: () -> Unit) {
    val kandang = _namaKandang.value.trim()
    val jalan = _alamatJalan.value.trim()
    val kec = _kecamatan.value.trim()
    val kab = _kabupaten.value.trim()

    if (kandang.isEmpty()) {
      _formError.value = "Nama Kandang wajib diisi"
      return
    }
    if (jalan.isEmpty()) {
      _formError.value = "Alamat Jalan wajib diisi"
      return
    }
    if (kec.isEmpty()) {
      _formError.value = "Kecamatan wajib diisi"
      return
    }
    if (kab.isEmpty()) {
      _formError.value = "Kabupaten wajib diisi"
      return
    }

    val grandTotal = calculateGrandTotal()
    val now = System.currentTimeMillis()
    val dateLocale = Locale("id", "ID")
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", dateLocale)
    val timeFormat = SimpleDateFormat("HH:mm 'WIB'", dateLocale)

    val dateStr = dateFormat.format(Date(now))
    val timeStr = timeFormat.format(Date(now))
    val setsJson = WeighingSerializer.setsToJson(_sets.value)

    val record = WeighingRecord(
      namaKandang = kandang,
      alamatJalan = jalan,
      kecamatan = kec,
      kabupaten = kab,
      timestamp = now,
      formattedDate = dateStr,
      formattedTime = timeStr,
      totalEkor = grandTotal.totalEkor,
      totalBeratKg = grandTotal.totalBeratKg,
      bobotRataRata = grandTotal.bobotRataRata,
      setsJson = setsJson
    )

    viewModelScope.launch {
      repository.insert(record)
      _showSaveDialog.value = false
      _toastMessage.emit("Data berhasil disimpan")
      resetWeighingSession()
      onSavedSuccessfully()
    }
  }

  // Detail & Delete
  fun selectRecordForDetail(record: WeighingRecord?) {
    _selectedRecord.value = record
  }

  fun deleteRecord(record: WeighingRecord) {
    viewModelScope.launch {
      repository.delete(record.id)
      if (_selectedRecord.value?.id == record.id) {
        _selectedRecord.value = null
      }
      _toastMessage.emit("Data kandang \"${record.namaKandang}\" telah dihapus")
    }
  }

  fun deleteAllRecords() {
    viewModelScope.launch {
      repository.deleteAll()
      _selectedRecord.value = null
      _toastMessage.emit("Semua data riwayat berhasil dibersihkan")
    }
  }
}
