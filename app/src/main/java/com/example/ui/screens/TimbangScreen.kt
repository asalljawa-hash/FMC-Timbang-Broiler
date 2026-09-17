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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeighingFormatter
import com.example.model.WeighingRow
import com.example.ui.components.SaveDataDialog
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
import com.example.ui.theme.InputBorder
import com.example.ui.theme.InputBorderFocused
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnGreenContainer
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.viewmodel.WeighingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimbangScreen(
  viewModel: WeighingViewModel,
  onNavigateBack: () -> Unit
) {
  val sets by viewModel.sets.collectAsState()
  val currentSetIndex by viewModel.currentSetIndex.collectAsState()
  val grandTotal = remember(sets) { viewModel.calculateGrandTotal() }
  val showSaveDialog by viewModel.showSaveDialog.collectAsState()
  var showQuickFillDialog by remember { mutableStateOf(false) }

  // Form input state for saving
  val namaKandang by viewModel.namaKandang.collectAsState()
  val alamatJalan by viewModel.alamatJalan.collectAsState()
  val kecamatan by viewModel.kecamatan.collectAsState()
  val kabupaten by viewModel.kabupaten.collectAsState()
  val formError by viewModel.formError.collectAsState()

  val currentSet = sets.getOrNull(currentSetIndex) ?: sets.first()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Tabel Data Timbang",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = GreenDarkText
            )
            Text(
              text = "10 baris per set • ${sets.size} set aktif",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("btn_back_timbang")
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
      contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
    ) {
      // SET SELECTOR TABS & MANAGEMENT
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
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
                  imageVector = Icons.Default.Layers,
                  contentDescription = null,
                  tint = GreenDarkText,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "HALAMAN / SET PENIMBANGAN",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText,
                  letterSpacing = 0.5.sp
                )
              }

              // Delete Set button if more than 1 set exists
              if (sets.size > 1) {
                IconButton(
                  onClick = { viewModel.removeSet(currentSetIndex) },
                  modifier = Modifier.size(28.dp).testTag("btn_delete_current_set")
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Hapus Set Ini",
                    tint = RedError,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              itemsIndexed(sets) { index, set ->
                val isSelected = index == currentSetIndex
                FilterChip(
                  selected = isSelected,
                  onClick = { viewModel.selectSet(index) },
                  label = {
                    Text(
                      text = "Set ${set.setNumber} (${set.totalEkor} ekor)",
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      fontSize = 13.sp
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenButton,
                    selectedLabelColor = PureWhite,
                    containerColor = GreenContainer,
                    labelColor = GreenDarkText
                  ),
                  border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) GreenButton else GreenContainerBorder
                  ),
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.testTag("chip_set_${index + 1}")
                )
              }

              // Add Set chip
              item {
                OutlinedButton(
                  onClick = { viewModel.addNewSet() },
                  shape = RoundedCornerShape(16.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                  border = BorderStroke(1.dp, AmberAccent),
                  colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AmberContainer
                  ),
                  modifier = Modifier.height(32.dp).testTag("btn_quick_add_set")
                ) {
                  Text(
                    "+ Set Baru",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent
                  )
                }
              }
            }
          }
        }
      }

      // QUICK FILL HELPER
      item {
        Surface(
          onClick = { showQuickFillDialog = true },
          color = AmberContainer,
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, AmberBorder),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("card_quick_fill_ekor")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.FlashOn,
                  contentDescription = null,
                  tint = OrangeAccent,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "ISI CEPAT JUMLAH EKOR",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = OnAmberContainer,
                  letterSpacing = 0.5.sp
                )
              }

              Text(
                text = "Pilih jumlah ekor untuk mengisi seluruh baris pada set aktif.",
                style = MaterialTheme.typography.bodySmall,
                color = OnAmberContainer.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
              onClick = { showQuickFillDialog = true },
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = AmberAccent,
                contentColor = PureWhite
              ),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
              modifier = Modifier
                .height(38.dp)
                .testTag("btn_select_quick_fill_ekor")
            ) {
              Text(
                text = "Pilih Ekor",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // WEIGHING TABLE CARD (Clean, distinct, high-contrast)
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(20.dp),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Table Header Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(GreenContainer, RoundedCornerShape(10.dp))
                .padding(vertical = 8.dp, horizontal = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "NO",
                modifier = Modifier.width(30.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText,
                textAlign = TextAlign.Center
              )
              Text(
                text = "EKOR",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText,
                textAlign = TextAlign.Center
              )
              Text(
                text = "BERAT (KG)",
                modifier = Modifier.weight(1.35f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText,
                textAlign = TextAlign.Center
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 10 Rows with comfortable spacing
            val focusManager = LocalFocusManager.current
            currentSet.rows.forEachIndexed { rowIndex, row ->
              TableRowItem(
                row = row,
                onEkorChanged = { newEkor ->
                  viewModel.updateRow(rowIndex, newEkor, row.berat)
                },
                onBeratChanged = { newBerat ->
                  viewModel.updateRow(rowIndex, row.ekor, newBerat)
                },
                onNext = {
                  focusManager.moveFocus(FocusDirection.Down)
                }
              )
            }
          }
        }
      }

      // REAL-TIME CALCULATION CARD (High visual focus: JUMLAH EKOR, TOTAL BERAT, BOBOT RATA-RATA)
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          shape = RoundedCornerShape(20.dp),
          border = BorderStroke(1.5.dp, GreenContainerBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("card_calculation_result")
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // Header Bar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .background(GreenContainer, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = GreenDarkText,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Text(
                  text = if (sets.size > 1) "TOTAL KESELURUHAN (${sets.size} Set)" else "HASIL PERHITUNGAN",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText,
                  letterSpacing = 0.5.sp
                )
              }

              Surface(
                color = GreenContainer,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GreenContainerBorder)
              ) {
                Text(
                  text = "OTOMATIS",
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = GreenDarkText,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
              }
            }

            HorizontalDivider(color = DividerColor)

            // Primary Metrics: JUMLAH EKOR & TOTAL BERAT
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // 1. JUMLAH EKOR
              Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "JUMLAH EKOR",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText,
                  letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${grandTotal.totalEkor}",
                  style = MaterialTheme.typography.headlineMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText
                )
                Text(
                  text = "ekor ayam",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }

              Box(
                modifier = Modifier
                  .width(1.dp)
                  .height(50.dp)
                  .background(DividerColor)
              )

              // 2. TOTAL BERAT
              Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "TOTAL BERAT",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText,
                  letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = WeighingFormatter.formatTonase(grandTotal.totalBeratKg),
                  style = MaterialTheme.typography.headlineMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenDarkText
                )
                Text(
                  text = "kilogram (kg)",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              }
            }

            // 3. BOBOT RATA-RATA (Hero Visual Focus)
            Surface(
              color = AmberContainer,
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, AmberBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Text(
                  text = "BOBOT RATA-RATA",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = OrangeAccent,
                  letterSpacing = 1.sp
                )
                Text(
                  text = String.format(Locale.US, "%.3f", grandTotal.bobotRataRata) + " kg/ekor",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = TextDarkPrimary
                )
                if (grandTotal.totalEkor > 0) {
                  Text(
                    text = "${WeighingFormatter.formatTonase(grandTotal.totalBeratKg)} kg ÷ ${grandTotal.totalEkor} ekor",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnAmberContainer,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // BOTTOM ACTION BUTTONS:
      // Clearly different styling:
      // 1. TAMBAH SET (Secondary, Amber/Orange outline button, NO double "+")
      // 2. SIMPAN DATA (Primary, Vibrant Green button, elevated)
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Tombol Tambah Set Berikutnya (Single "+", no double "+")
          OutlinedButton(
            onClick = { viewModel.addNewSet() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = AmberContainer,
              contentColor = OrangeAccent
            ),
            border = BorderStroke(1.5.dp, AmberAccent),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("btn_tambah_set")
          ) {
            Text(
              text = "+ TAMBAH SET / HALAMAN BERIKUTNYA",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp,
              color = OrangeAccent,
              letterSpacing = 0.5.sp
            )
          }

          // Tombol SIMPAN DATA (Primary standout button)
          Button(
            onClick = { viewModel.onSaveButtonClicked() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = GreenButton,
              contentColor = PureWhite
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("btn_simpan_data")
          ) {
            Icon(
              imageVector = Icons.Default.Save,
              contentDescription = null,
              tint = PureWhite,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SIMPAN DATA",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = PureWhite,
              letterSpacing = 0.5.sp
            )
          }
        }
      }
    }
  }

  // Save Modal Dialog
  SaveDataDialog(
    show = showSaveDialog,
    calculation = grandTotal,
    namaKandang = namaKandang,
    alamatJalan = alamatJalan,
    kecamatan = kecamatan,
    kabupaten = kabupaten,
    formError = formError,
    onNamaKandangChange = { viewModel.updateNamaKandang(it) },
    onAlamatJalanChange = { viewModel.updateAlamatJalan(it) },
    onKecamatanChange = { viewModel.updateKecamatan(it) },
    onKabupatenChange = { viewModel.updateKabupaten(it) },
    onDismiss = { viewModel.dismissSaveDialog() },
    onSave = {
      viewModel.submitSave(
        onSavedSuccessfully = {
          onNavigateBack()
        }
      )
    }
  )

  // Quick Fill Ekor Modal Dialog
  QuickFillEkorDialog(
    show = showQuickFillDialog,
    currentSetNumber = currentSet.setNumber,
    onDismiss = { showQuickFillDialog = false },
    onEkorSelected = { selectedEkor ->
      viewModel.fillDefaultEkorForCurrentSet(selectedEkor)
    }
  )
}

@Composable
fun QuickFillEkorDialog(
  show: Boolean,
  currentSetNumber: Int,
  onDismiss: () -> Unit,
  onEkorSelected: (String) -> Unit
) {
  if (!show) return

  val ekorOptions = listOf("15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25")

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(AmberContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.FlashOn,
          contentDescription = null,
          tint = OrangeAccent,
          modifier = Modifier.size(24.dp)
        )
      }
    },
    title = {
      Text(
        text = "ISI CEPAT JUMLAH EKOR",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = TextDarkPrimary,
        modifier = Modifier.fillMaxWidth()
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Pilih jumlah ekor untuk mengisi seluruh baris pada set aktif (Set $currentSetNumber).",
          style = MaterialTheme.typography.bodyMedium,
          color = TextDarkSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        // Grid of numbers 15 to 25: 4 columns
        val chunked = ekorOptions.chunked(4)
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          chunked.forEach { rowOptions ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowOptions.forEach { ekor ->
                OutlinedButton(
                  onClick = {
                    onEkorSelected(ekor)
                    onDismiss()
                  },
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.5.dp, AmberAccent),
                  colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AmberContainer.copy(alpha = 0.4f),
                    contentColor = OrangeAccent
                  ),
                  contentPadding = PaddingValues(0.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_ekor_$ekor")
                ) {
                  Text(
                    text = ekor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OrangeAccent
                  )
                }
              }
              repeat(4 - rowOptions.size) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("btn_cancel_quick_fill")
      ) {
        Text(
          text = "Batal",
          color = TextMuted,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    },
    shape = RoundedCornerShape(20.dp),
    containerColor = PureWhite
  )
}

@Composable
fun TableRowItem(
  row: WeighingRow,
  onEkorChanged: (String) -> Unit,
  onBeratChanged: (String) -> Unit,
  onNext: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.5.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Row Number Badge (Compact)
    Box(
      modifier = Modifier
        .width(30.dp)
        .height(28.dp)
        .background(
          if (row.isValid) GreenContainer else Color(0xFFF1F5F2),
          RoundedCornerShape(6.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "${row.no}",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
        color = if (row.isValid) GreenDarkText else TextMuted
      )
    }

    // Input Ekor (Clear white input, distinct border, bold font, green on focus)
    OutlinedTextField(
      value = row.ekor,
      onValueChange = { input ->
        if (input.isEmpty() || input.all { it.isDigit() }) {
          onEkorChanged(input)
        }
      },
      placeholder = {
        Text(
          "0",
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
          fontSize = 16.sp,
          color = TextMuted.copy(alpha = 0.6f)
        )
      },
      singleLine = true,
      textStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextDarkPrimary
      ),
      shape = RoundedCornerShape(10.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = InputBorderFocused,
        unfocusedBorderColor = InputBorder,
        focusedContainerColor = PureWhite,
        unfocusedContainerColor = PureWhite,
        cursorColor = InputBorderFocused,
        focusedTextColor = TextDarkPrimary,
        unfocusedTextColor = TextDarkPrimary
      ),
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
      ),
      keyboardActions = KeyboardActions(onNext = { onNext() }),
      modifier = Modifier
        .weight(1f)
        .height(48.dp)
        .testTag("input_ekor_${row.no}")
    )

    // Input Berat (KG) (Clear white input, distinct border, bold font, green on focus, retains decimals like 49.20)
    OutlinedTextField(
      value = row.berat,
      onValueChange = { input ->
        val normalized = input.replace(',', '.')
        if (normalized.isEmpty() || normalized.matches(Regex("""^\d*\.?\d*$"""))) {
          onBeratChanged(normalized)
        }
      },
      placeholder = {
        Text(
          "0.00",
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
          fontSize = 16.sp,
          color = TextMuted.copy(alpha = 0.6f)
        )
      },
      singleLine = true,
      textStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = if (row.beratDouble > 0.0) GreenDarkText else TextDarkPrimary
      ),
      shape = RoundedCornerShape(10.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = InputBorderFocused,
        unfocusedBorderColor = InputBorder,
        focusedContainerColor = PureWhite,
        unfocusedContainerColor = PureWhite,
        cursorColor = InputBorderFocused,
        focusedTextColor = TextDarkPrimary,
        unfocusedTextColor = TextDarkPrimary
      ),
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal,
        imeAction = ImeAction.Next
      ),
      keyboardActions = KeyboardActions(onNext = { onNext() }),
      modifier = Modifier
        .weight(1.35f)
        .height(48.dp)
        .testTag("input_berat_${row.no}")
    )
  }
}
