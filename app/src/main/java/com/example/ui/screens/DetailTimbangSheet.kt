package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeighingRecord
import com.example.model.WeighingFormatter
import com.example.model.WeighingSerializer
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
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.util.PdfGenerator
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTimbangSheet(
  record: WeighingRecord?,
  onDismiss: () -> Unit,
  onDelete: (WeighingRecord) -> Unit
) {
  if (record == null) return

  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var showDeleteDialog by remember { mutableStateOf(false) }
  val sets = remember(record.setsJson) {
    WeighingSerializer.setsFromJson(record.setsJson)
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = Modifier.testTag("detail_timbang_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header Bar
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
              .size(42.dp)
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
          Column {
            Text(
              text = record.namaKandang,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = GreenDarkText
            )
            Text(
              text = "Rincian Hasil Penimbangan",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Tutup Detail", tint = TextDarkPrimary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 1. INFORMASI KANDANG & ALAMAT (Mudah dibaca)
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = GreenDarkText,
              modifier = Modifier.size(18.dp)
            )
            Column {
              Text(
                text = "Lokasi Kandang:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${record.alamatJalan}, Kec. ${record.kecamatan}, Kab. ${record.kabupaten}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextDarkPrimary
              )
            }
          }

          HorizontalDivider(color = DividerColor)

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
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = record.formattedDate,
                style = MaterialTheme.typography.bodySmall,
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
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = record.formattedTime,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. RINGKASAN TOTAL DALAM KARTU
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, GreenContainerBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "TOTAL EKOR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
              Text(
                text = WeighingFormatter.formatEkor(record.totalEkor),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
              Text(
                text = "ekor",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(DividerColor)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "TOTAL BERAT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
              Text(
                text = WeighingFormatter.formatKg(record.totalBeratKg),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
              Text(
                text = "kilogram",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }
          }

          // Bobot Rata-rata Highlight
          Surface(
            color = AmberContainer,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AmberBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "BOBOT RATA-RATA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = OrangeAccent
              )
              Text(
                text = String.format(Locale.US, "%.3f", record.bobotRataRata) + " kg/ekor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextDarkPrimary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. RINCIAN SETIAP SET & TABEL DETAIL
      Text(
        text = "RINCIAN PER SET (${sets.size} Set)",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        color = GreenDarkText,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f, fill = false)
          .height(260.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(sets) { set ->
          Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              // Set Subheader
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Set #${set.setNumber}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp,
                  color = GreenDarkText
                )
                Text(
                  text = "${set.totalEkor} ekor | ${WeighingFormatter.formatKg(set.totalBeratKg)}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = OrangeAccent
                )
              }

              HorizontalDivider(
                color = DividerColor,
                modifier = Modifier.padding(vertical = 6.dp)
              )

              // Valid weighing rows
              val validRows = set.rows.filter { it.isValid }
              if (validRows.isEmpty()) {
                Text(
                  text = "Tidak ada baris timbangan di set ini",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMuted
                )
              } else {
                validRows.forEach { row ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Timbang #${row.no}",
                      style = MaterialTheme.typography.bodySmall,
                      color = TextDarkSecondary
                    )
                    Text(
                      text = "${row.ekor} ekor = ${WeighingFormatter.formatKg(row.beratDouble)}",
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Bold,
                      color = TextDarkPrimary
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons: DOWNLOAD PDF & BAGIKAN PDF
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // 1. DOWNLOAD PDF
        Button(
          onClick = {
            val (success, message) = PdfGenerator.downloadPdf(context, record, sets)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = GreenButton,
            contentColor = PureWhite
          ),
          shape = RoundedCornerShape(14.dp),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("btn_download_pdf")
        ) {
          Text(text = "📄", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DOWNLOAD PDF",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = PureWhite
          )
        }

        // 2. BAGIKAN PDF
        Button(
          onClick = {
            PdfGenerator.sharePdf(context, record, sets)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = OrangeAccent,
            contentColor = PureWhite
          ),
          shape = RoundedCornerShape(14.dp),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("btn_share_pdf")
        ) {
          Text(text = "📤", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "BAGIKAN PDF",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = PureWhite
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Delete Record Action Button
      OutlinedButton(
        onClick = { showDeleteDialog = true },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
        border = BorderStroke(1.dp, RedError.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("btn_delete_record_from_detail")
      ) {
        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Hapus Riwayat Ini", fontWeight = FontWeight.Bold)
      }
    }
  }

  // Delete Confirm Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      icon = {
        Icon(Icons.Default.Delete, contentDescription = null, tint = RedError)
      },
      title = { Text("Hapus Rekap Penimbangan?") },
      text = {
        Text("Data penimbangan untuk \"${record.namaKandang}\" pada tanggal ${record.formattedDate} akan dihapus secara permanen dari perangkat.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            onDismiss()
            onDelete(record)
          },
          colors = ButtonDefaults.textButtonColors(contentColor = RedError)
        ) {
          Text("Ya, Hapus", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Batal")
        }
      }
    )
  }
}
