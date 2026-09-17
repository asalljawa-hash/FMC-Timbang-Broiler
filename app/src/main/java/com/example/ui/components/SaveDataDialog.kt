package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.model.TotalCalculation
import com.example.model.WeighingFormatter
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GreenButton
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenContainerBorder
import com.example.ui.theme.GreenDarkText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted

@Composable
fun SaveDataDialog(
  show: Boolean,
  calculation: TotalCalculation,
  namaKandang: String,
  alamatJalan: String,
  kecamatan: String,
  kabupaten: String,
  formError: String?,
  onNamaKandangChange: (String) -> Unit,
  onAlamatJalanChange: (String) -> Unit,
  onKecamatanChange: (String) -> Unit,
  onKabupatenChange: (String) -> Unit,
  onDismiss: () -> Unit,
  onSave: () -> Unit
) {
  if (!show) return

  val focusManager = LocalFocusManager.current

  AlertDialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier
      .padding(horizontal = 20.dp, vertical = 24.dp)
      .fillMaxWidth(),
    title = null,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Dialog Header
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
                .background(GreenContainer, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = GreenDarkText,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = "Simpan Data Timbang",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
              Text(
                text = "Lengkapi detail lokasi penimbangan",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup Dialog",
              tint = TextDarkPrimary
            )
          }
        }

        // Summary Preview Card
        Card(
          colors = CardDefaults.cardColors(containerColor = GreenContainer),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, GreenContainerBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.Start) {
              Text(
                text = "Total Ekor",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkSecondary,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = WeighingFormatter.formatEkor(calculation.totalEkor),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Total Berat",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkSecondary,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = WeighingFormatter.formatKg(calculation.totalBeratKg),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = GreenDarkText
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Rata-rata",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkSecondary,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = WeighingFormatter.formatAvgKg(calculation.bobotRataRata),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AmberAccent
              )
            }
          }
        }

        if (formError != null) {
          Text(
            text = formError,
            color = RedError,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
          )
        }

        // Input Fields
        OutlinedTextField(
          value = namaKandang,
          onValueChange = onNamaKandangChange,
          label = { Text("Nama Kandang *") },
          placeholder = { Text("Contoh: Kandang Broiler Blok A-1") },
          leadingIcon = {
            Icon(Icons.Default.Business, contentDescription = null, tint = GreenDarkText)
          },
          isError = formError?.contains("Kandang", ignoreCase = true) == true,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenButton,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_nama_kandang"),
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
          )
        )

        OutlinedTextField(
          value = alamatJalan,
          onValueChange = onAlamatJalanChange,
          label = { Text("Alamat Jalan *") },
          placeholder = { Text("Contoh: Jl. Raya Peternakan No. 45") },
          leadingIcon = {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenDarkText)
          },
          isError = formError?.contains("Jalan", ignoreCase = true) == true,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenButton,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_alamat_jalan"),
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
          )
        )

        OutlinedTextField(
          value = kecamatan,
          onValueChange = onKecamatanChange,
          label = { Text("Kecamatan *") },
          placeholder = { Text("Contoh: Cikembar") },
          leadingIcon = {
            Icon(Icons.Default.Map, contentDescription = null, tint = GreenDarkText)
          },
          isError = formError?.contains("Kecamatan", ignoreCase = true) == true,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenButton,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_kecamatan"),
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
          )
        )

        OutlinedTextField(
          value = kabupaten,
          onValueChange = onKabupatenChange,
          label = { Text("Kabupaten *") },
          placeholder = { Text("Contoh: Sukabumi") },
          leadingIcon = {
            Icon(Icons.Default.LocationCity, contentDescription = null, tint = GreenDarkText)
          },
          isError = formError?.contains("Kabupaten", ignoreCase = true) == true,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenButton,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = PureWhite,
            unfocusedContainerColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_kabupaten"),
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onDone = {
              focusManager.clearFocus()
              onSave()
            }
          )
        )

        Text(
          text = "Tanggal & jam dicatat otomatis dari sistem perangkat.",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onSave,
        colors = ButtonDefaults.buttonColors(
          containerColor = GreenButton,
          contentColor = PureWhite
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_konfirmasi_simpan")
      ) {
        Icon(
          imageVector = Icons.Default.Save,
          contentDescription = null,
          tint = PureWhite,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "SIMPAN SEKARANG",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 15.sp,
          color = PureWhite
        )
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
      ) {
        Text("Batal", color = TextDarkSecondary, fontWeight = FontWeight.SemiBold)
      }
    },
    shape = RoundedCornerShape(20.dp),
    containerColor = PureWhite
  )
}
