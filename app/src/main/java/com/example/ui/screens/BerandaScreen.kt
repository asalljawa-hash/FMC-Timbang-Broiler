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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeighingRecord
import com.example.model.WeighingFormatter
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DividerColor
import com.example.ui.theme.GreenButton
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenContainerBorder
import com.example.ui.theme.GreenDarkText
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.viewmodel.WeighingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(
  viewModel: WeighingViewModel,
  onMulaiMenimbang: () -> Unit,
  onDataTersimpan: () -> Unit,
  onPengaturan: () -> Unit
) {
  val records by viewModel.savedRecords.collectAsState()
  val selectedRecord by viewModel.selectedRecord.collectAsState()

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 36.dp)
    ) {
      // 1. HEADER & BRANDING APLIKASI
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(GreenContainer, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Scale,
                contentDescription = null,
                tint = GreenDarkText,
                modifier = Modifier.size(24.dp)
              )
            }
            Text(
              text = "TIMBANG BROILER",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = GreenDarkText,
              letterSpacing = 0.5.sp
            )
          }

          // Offline Badge
          Surface(
            color = GreenContainer,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GreenContainerBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = GreenDarkText,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = "100% OFFLINE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = GreenDarkText,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      // 2. JUDUL KALKULATOR TIMBANG AYAM BROILER & 3. DESKRIPSI SINGKAT
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(20.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "KALKULATOR TIMBANG AYAM BROILER",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = GreenDarkText,
              lineHeight = 26.sp
            )
            Text(
              text = "Alat pencatatan dan perhitungan hasil penimbangan ayam broiler yang praktis, akurat, dan mudah digunakan untuk operator lapangan, peternak, pembeli, dan pengguna lainnya.",
              style = MaterialTheme.typography.bodyMedium,
              color = TextDarkSecondary,
              lineHeight = 20.sp
            )
          }
        }
      }

      // 4. TOMBOL UTAMA: "MULAI MENIMBANG" (HERO ACTION - Paling menonjol dan mudah disentuh)
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = GreenButton),
          shape = RoundedCornerShape(20.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMulaiMenimbang)
            .testTag("btn_mulai_menimbang")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .background(PureWhite.copy(alpha = 0.22f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Scale,
                  contentDescription = null,
                  tint = PureWhite,
                  modifier = Modifier.size(28.dp)
                )
              }
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = "MULAI MENIMBANG",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = PureWhite,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Buka tabel data timbang (10 baris/set)",
                  style = MaterialTheme.typography.bodySmall,
                  color = PureWhite.copy(alpha = 0.9f)
                )
              }
            }

            Box(
              modifier = Modifier
                .size(36.dp)
                .background(PureWhite.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // 5. TOMBOL "DATA TERSIMPAN"
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDataTersimpan)
            .testTag("btn_data_tersimpan")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .background(GreenContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.FolderOpen,
                  contentDescription = null,
                  tint = GreenDarkText,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "DATA TERSIMPAN",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = TextDarkPrimary
                )
                Text(
                  text = "Lihat riwayat hasil timbang tersimpan",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                color = AmberContainer,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AmberBorder)
              ) {
                Text(
                  text = "${records.size} Rekap",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = OnAmberContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
              Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }

      // 6. TOMBOL "PENGATURAN"
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPengaturan)
            .testTag("btn_pengaturan")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .background(AmberContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  tint = AmberAccent,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "PENGATURAN",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = TextDarkPrimary
                )
                Text(
                  text = "Petunjuk rumus & info aplikasi",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }
            }

            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }

      // 7. DAFTAR REKAP TERBARU
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "REKAP TERBARU",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = GreenDarkText,
            letterSpacing = 0.5.sp
          )

          if (records.isNotEmpty()) {
            Text(
              text = "${records.size} data",
              style = MaterialTheme.typography.labelSmall,
              color = TextMuted
            )
          }
        }
      }

      if (records.isEmpty()) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(GreenContainer, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Scale,
                  contentDescription = null,
                  tint = GreenDarkText,
                  modifier = Modifier.size(24.dp)
                )
              }
              Text(
                text = "Belum Ada Data Timbang Tersimpan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextDarkPrimary
              )
              Text(
                text = "Tekan tombol MULAI MENIMBANG di atas untuk mencatat data penimbangan kandang Anda.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(records, key = { it.id }) { record ->
          WeighingRecordCard(
            record = record,
            onClick = { viewModel.selectRecordForDetail(record) },
            onDelete = { viewModel.deleteRecord(record) }
          )
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
