package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.WeighingRecord
import com.example.model.WeighingRow
import com.example.model.WeighingSet
import com.example.util.PdfGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FMC TIMBANG BROILER", appName)
  }

  @Test
  fun `verify calculation precision according to prompt requirements`() {
    // 16 ekor = 49.20 kg, 16 ekor = 50.00 kg, 16 ekor = 50.10 kg
    val row1 = WeighingRow(no = 1, ekor = "16", berat = "49.20")
    val row2 = WeighingRow(no = 2, ekor = "16", berat = "50.00")
    val row3 = WeighingRow(no = 3, ekor = "16", berat = "50.10")

    val set = WeighingSet(
      setNumber = 1,
      rows = listOf(row1, row2, row3)
    )

    assertEquals(48, set.totalEkor)
    assertEquals(149.30, set.totalBeratKg, 0.001)

    // Formula test from prompt: 491.830 kg / 161 ekor = ~3.055 kg/ekor
    val totalBerat = 491.830
    val totalEkor = 161
    val avg = totalBerat / totalEkor
    assertEquals(3.055, avg, 0.001)
  }

  @Test
  fun `verify quick fill ekor updates all rows in current set and preserves berat`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.WeighingViewModel(application)

    // Initially Set 1 has 10 rows with empty ekor and empty berat
    assertEquals(1, viewModel.sets.value.size)
    assertEquals(0, viewModel.sets.value[0].totalEkor)

    // Set row 1 berat to 49.20
    viewModel.updateRow(0, "16", "49.20")
    assertEquals("49.20", viewModel.sets.value[0].rows[0].berat)

    // Perform quick fill with 18
    viewModel.fillDefaultEkorForCurrentSet("18")

    val set1 = viewModel.sets.value[0]
    // All 10 rows must now have 18 ekor
    assertEquals(10, set1.rows.size)
    set1.rows.forEach { row ->
      assertEquals("18", row.ekor)
    }
    // Berat on row 0 must remain 49.20
    assertEquals("49.20", set1.rows[0].berat)
    // Total ekor is 10 * 18 = 180
    assertEquals(180, set1.totalEkor)

    // User can manually edit row 0 afterwards
    viewModel.updateRow(0, "17", "49.20")
    assertEquals("17", viewModel.sets.value[0].rows[0].ekor)
    assertEquals(179, viewModel.sets.value[0].totalEkor)
  }

  @Test
  fun `verify PDF file name sanitization format`() {
    val name1 = PdfGenerator.getSanitizedFileName("Kandang Barokah #1", "17 September 2026")
    assertEquals("Timbang_Broiler_Kandang_Barokah_1_17_September_2026.pdf", name1)

    val name2 = PdfGenerator.getSanitizedFileName("PT. Unggas Makmur / Blok A", "17/09/2026")
    assertEquals("Timbang_Broiler_PT_Unggas_Makmur_Blok_A_17_09_2026.pdf", name2)

    val name3 = PdfGenerator.getSanitizedFileName("", "")
    assertTrue(name3.startsWith("Timbang_Broiler_"))
    assertTrue(name3.endsWith(".pdf"))

    val name4 = PdfGenerator.getSanitizedFileName("###@@@!!!", "$$$%%%")
    assertTrue(name4.startsWith("Timbang_Broiler_"))
    assertTrue(name4.endsWith(".pdf"))
  }

  @Test
  fun `verify WeighingSerializer round trip for PDF data source`() {
    val row1 = WeighingRow(no = 1, ekor = "16", berat = "49.20")
    val row2 = WeighingRow(no = 2, ekor = "16", berat = "50.00")
    val set1 = WeighingSet(setNumber = 1, rows = listOf(row1, row2))

    val json = com.example.model.WeighingSerializer.setsToJson(listOf(set1))
    val parsedSets = com.example.model.WeighingSerializer.setsFromJson(json)

    assertEquals(1, parsedSets.size)
    assertEquals(1, parsedSets[0].setNumber)
    assertEquals(10, parsedSets[0].rows.size)
    assertEquals(32, parsedSets[0].totalEkor)
    assertEquals(99.20, parsedSets[0].totalBeratKg, 0.001)
  }
}
