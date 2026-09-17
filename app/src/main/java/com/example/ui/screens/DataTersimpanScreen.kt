package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeighingRecord
import com.example.model.WeighingFormatter
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DividerColor
import com.example.ui.theme.GreenButton
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenContainerBorder
import com.example.ui.theme.GreenDarkText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.viewmodel.WeighingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTersimpanScreen(
  viewModel: WeighingViewModel,
  onNavigateBack: () -> Unit,
  onMulaiMenimbang: () -> Unit
) {
  val records by viewModel.savedRecords.collectAsState()
  val selectedRecord by viewModel.selectedRecord.collectAsState()
  var searchQuery by remember { mutableStateOf("") }

  val filteredRecords = remember(records, searchQuery) {
    if (searchQuery.isBlank()) {
      records
    } else {
      records.filter {
        it.namaKandang.contains(searchQuery, ignoreCase = true) ||
          it.kecamatan.contains(searchQuery, ignoreCase = true) ||
          it.kabupaten.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Data Tersimpan",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = GreenDarkText
            )
            Text(
              text = "${records.size} rekap tersimpan di perangkat",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("btn_back_data_tersimpan")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Kembali ke Beranda",
              tint = TextDarkPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = PureWhite
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp)
    ) {
      // Search Bar if records exist
      if (records.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Cari nama kandang atau wilayah...", color = TextMuted) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = GreenDarkText)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Hapus pencarian")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenButton,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_search_records")
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (records.isEmpty()) {
        // Empty state
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .background(GreenContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                tint = GreenDarkText,
                modifier = Modifier.size(38.dp)
              )
            }
            Text(
              text = "Belum Ada Data Tersimpan",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextDarkPrimary
            )
            Text(
              text = "Hasil penimbangan ayam broiler yang Anda simpan akan tampil di sini secara offline dan permanen.",
              style = MaterialTheme.typography.bodyMedium,
              color = TextDarkSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
              onClick = onMulaiMenimbang,
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GreenButton),
              modifier = Modifier
                .height(48.dp)
                .testTag("btn_empty_start_weighing")
            ) {
              Icon(Icons.Default.Add, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("MULAI MENIMBANG SEKARANG", fontWeight = FontWeight.Bold)
            }
          }
        }
      } else if (filteredRecords.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Tidak ditemukan data dengan kata kunci \"$searchQuery\"",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDarkSecondary
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 32.dp)
        ) {
          items(filteredRecords, key = { it.id }) { record ->
            WeighingRecordCard(
              record = record,
              onClick = { viewModel.selectRecordForDetail(record) },
              onDelete = { viewModel.deleteRecord(record) }
            )
          }
        }
      }
    }
  }

  // Detail Sheet
  DetailTimbangSheet(
    record = selectedRecord,
    onDismiss = { viewModel.selectRecordForDetail(null) },
    onDelete = { record ->
      viewModel.deleteRecord(record)
    }
  )
}

@Composable
fun WeighingRecordCard(
  record: WeighingRecord,
  onClick: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, CardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("card_record_${record.id}")
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header: Nama Kandang (Informasi Utama) & Button "Lihat Detail"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = record.namaKandang,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = GreenDarkText
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${record.kecamatan}, ${record.kabupaten}",
              style = MaterialTheme.typography.bodySmall,
              color = TextDarkSecondary
            )
          }
        }

        // Tombol "Lihat Detail" (Clear pill button)
        Surface(
          color = GreenContainer,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, GreenContainerBorder)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = null,
              tint = GreenDarkText,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Lihat Detail",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = GreenDarkText
            )
          }
        }
      }

      HorizontalDivider(color = DividerColor)

      // 3 Main Metric Pillars: Ekor, Berat, Rata-rata
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Total Ekor
        Column(horizontalAlignment = Alignment.Start) {
          Text(
            text = "Total Ekor",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = WeighingFormatter.formatEkor(record.totalEkor),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = GreenDarkText
          )
        }

        // Total Berat
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Total Berat",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = WeighingFormatter.formatKg(record.totalBeratKg),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = GreenDarkText
          )
        }

        // Bobot Rata-rata
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Bobot Rata-rata",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = String.format(Locale.US, "%.3f kg", record.bobotRataRata),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AmberAccent
          )
        }
      }

      HorizontalDivider(color = DividerColor)

      // Footer: Date and Time
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = GreenDarkText,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = record.formattedDate,
            style = MaterialTheme.typography.labelSmall,
            color = TextDarkSecondary,
            fontWeight = FontWeight.Medium
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = AmberAccent,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = record.formattedTime,
            style = MaterialTheme.typography.labelSmall,
            color = TextDarkSecondary,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
