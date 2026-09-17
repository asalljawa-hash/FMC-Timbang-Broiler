package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.viewmodel.WeighingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanScreen(
  viewModel: WeighingViewModel,
  onNavigateBack: () -> Unit
) {
  val records by viewModel.savedRecords.collectAsState()
  var showDeleteAllDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Pengaturan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = GreenDarkText
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("btn_back_pengaturan")
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
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 36.dp)
    ) {
      // 1. INFORMASI OFFLINE LOKAL
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(GreenContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  tint = GreenDarkText,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "Penyimpanan Offline Lokal",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText
                )
                Text(
                  text = "100% tersimpan di perangkat tanpa internet",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }
            }

            HorizontalDivider(color = DividerColor)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Jumlah Data Tersimpan",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDarkPrimary
              )
              Surface(
                color = GreenContainer,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GreenContainerBorder)
              ) {
                Text(
                  text = "${records.size} Rekap",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  color = GreenDarkText,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = GreenButton,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Data tersimpan permanen di memori lokal perangkat.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary
              )
            }
          }
        }
      }

      // 2. RUMUS PERHITUNGAN
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(AmberContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Calculate,
                  contentDescription = null,
                  tint = AmberAccent,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "Standar Rumus Perhitungan",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText
                )
                Text(
                  text = "Petunjuk perhitungan baku lapangan",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }
            }

            HorizontalDivider(color = DividerColor)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              GuideItem(
                title = "1. Bobot Rata-rata (Average Body Weight)",
                desc = "TOTAL BERAT (kg) dibagi JUMLAH EKOR ayam. Dihitung secara real-time dan akurat hingga 3 digit desimal."
              )
              GuideItem(
                title = "2. Batas Baris per Set",
                desc = "Setiap set/halaman berisi 10 baris timbang. Jika melebihi 10 baris, gunakan tombol TAMBAH SET BERIKUTNYA."
              )
              GuideItem(
                title = "3. Penggabungan Multi-Set",
                desc = "Semua baris timbangan dari seluruh set akan otomatis diakumulasikan saat menekan tombol SIMPAN DATA."
              )
            }
          }
        }
      }

      // 3. INFORMASI APLIKASI
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(GreenContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = null,
                  tint = GreenDarkText,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "TIMBANG BROILER",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText
                )
                Text(
                  text = "Versi 1.0.0 (Native Android Jetpack Compose)",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }
            }

            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))

            Text(
              text = "Alat pencatatan dan perhitungan hasil penimbangan ayam broiler yang praktis, akurat, dan mudah digunakan.",
              style = MaterialTheme.typography.bodySmall,
              color = TextDarkSecondary,
              lineHeight = 18.sp
            )
          }
        }
      }

      // 4. BERSIHKAN DATA
      if (records.isNotEmpty()) {
        item {
          OutlinedButton(
            onClick = { showDeleteAllDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
            border = BorderStroke(1.dp, RedError.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("btn_bersihkan_semua_data")
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("BERSIHKAN SEMUA DATA TERSIMPAN", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }

  if (showDeleteAllDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteAllDialog = false },
      icon = {
        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = RedError)
      },
      title = { Text("Hapus Seluruh Riwayat?") },
      text = {
        Text("Tindakan ini akan menghapus seluruh ${records.size} riwayat penimbangan dari perangkat secara permanen.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteAllDialog = false
            viewModel.deleteAllRecords()
          },
          colors = ButtonDefaults.textButtonColors(contentColor = RedError)
        ) {
          Text("Ya, Hapus Semua", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteAllDialog = false }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
private fun GuideItem(title: String, desc: String) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.ExtraBold,
      color = GreenDarkText
    )
    Text(
      text = desc,
      style = MaterialTheme.typography.bodySmall,
      color = TextDarkSecondary,
      modifier = Modifier.padding(top = 2.dp),
      lineHeight = 18.sp
    )
  }
}
