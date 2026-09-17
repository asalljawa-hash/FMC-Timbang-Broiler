package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.WeighingRecord
import com.example.model.WeighingFormatter
import com.example.model.WeighingSet
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object PdfGenerator {

  /**
   * Generates a clean and safe filename for the PDF in the format:
   * Timbang_Broiler_NamaKandang_Tanggal.pdf
   */
  fun getSanitizedFileName(namaKandang: String, formattedDate: String): String {
    val cleanKandang = namaKandang
      .replace(Regex("[^a-zA-Z0-9_-]"), "_")
      .replace(Regex("_+"), "_")
      .trim('_')
      .ifEmpty { "Kandang" }

    val cleanDate = formattedDate
      .replace(Regex("[^a-zA-Z0-9_-]"), "_")
      .replace(Regex("_+"), "_")
      .trim('_')
      .ifEmpty { "Tanggal" }

    return "Timbang_Broiler_${cleanKandang}_${cleanDate}.pdf"
  }

  /**
   * Builds the native Android PdfDocument containing the complete weighing report.
   * Standard A4 size: 595 x 842 points.
   */
  fun createPdfDocument(record: WeighingRecord, sets: List<WeighingSet>): PdfDocument {
    val document = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

    // Paints
    val titlePaint = Paint().apply {
      isAntiAlias = true
      color = Color.WHITE
      textSize = 18f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val subtitlePaint = Paint().apply {
      isAntiAlias = true
      color = Color.argb(230, 255, 255, 255)
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    val sectionHeaderPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(27, 94, 32) // Forest green (#1B5E20)
      textSize = 10.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val labelPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(90, 100, 110)
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    val valuePaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(26, 26, 26)
      textSize = 9f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val cardTitlePaint = Paint().apply {
      isAntiAlias = true
      textSize = 7.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val cardValuePaint = Paint().apply {
      isAntiAlias = true
      textSize = 13f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val tableHeaderPaint = Paint().apply {
      isAntiAlias = true
      color = Color.WHITE
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val tableCellPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(30, 30, 30)
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    val tableCellBoldPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(20, 20, 20)
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val footerPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(120, 130, 140)
      textSize = 7.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    // Fill & Stroke Paints
    val greenBannerPaint = Paint().apply {
      color = Color.rgb(46, 125, 50) // Material Green 800 (#2E7D32)
      style = Paint.Style.FILL
    }

    val orangeAccentPaint = Paint().apply {
      color = Color.rgb(230, 81, 0) // Amber/Orange Accent (#E65100)
      style = Paint.Style.FILL
    }

    val lightGreenFillPaint = Paint().apply {
      color = Color.rgb(232, 245, 233) // Light Green (#E8F5E9)
      style = Paint.Style.FILL
    }

    val lightAmberFillPaint = Paint().apply {
      color = Color.rgb(255, 248, 225) // Light Amber (#FFF8E1)
      style = Paint.Style.FILL
    }

    val zebraFillPaint = Paint().apply {
      color = Color.rgb(249, 251, 249)
      style = Paint.Style.FILL
    }

    val cardBorderPaint = Paint().apply {
      color = Color.rgb(165, 214, 167) // Green Border (#A5D6A7)
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }

    val amberBorderPaint = Paint().apply {
      color = Color.rgb(255, 183, 77) // Amber Border (#FFB74D)
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }

    val grayBorderPaint = Paint().apply {
      color = Color.rgb(215, 220, 225)
      style = Paint.Style.STROKE
      strokeWidth = 0.8f
    }

    val linePaint = Paint().apply {
      color = Color.rgb(220, 225, 230)
      style = Paint.Style.STROKE
      strokeWidth = 0.8f
    }

    // Layout Margins
    val leftMargin = 36f
    val rightMargin = 559f
    val contentWidth = rightMargin - leftMargin // 523f

    var pageNumber = 1
    var currentPage = document.startPage(pageInfo)
    var canvas = currentPage.canvas
    var currentY = 36f

    fun drawPageHeader(c: Canvas, isFirstPage: Boolean): Float {
      if (isFirstPage) {
        // Banner Background
        c.drawRoundRect(RectF(leftMargin, 36f, rightMargin, 92f), 8f, 8f, greenBannerPaint)
        // Orange accent strip
        c.drawRect(leftMargin, 90f, rightMargin, 93f, orangeAccentPaint)

        // Title & Subtitle
        c.drawText("TIMBANG BROILER", leftMargin + 16f, 63f, titlePaint)
        c.drawText(
          "REKAPITULASI HASIL PENIMBANGAN AYAM BROILER",
          leftMargin + 16f,
          79f,
          subtitlePaint
        )
        return 108f
      } else {
        // Subsequent page top banner
        c.drawRoundRect(RectF(leftMargin, 36f, rightMargin, 66f), 6f, 6f, greenBannerPaint)
        c.drawRect(leftMargin, 64f, rightMargin, 66f, orangeAccentPaint)

        val continuationPaint = Paint().apply {
          isAntiAlias = true
          color = Color.WHITE
          textSize = 12f
          typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        c.drawText("TIMBANG BROILER - Lanjutan", leftMargin + 14f, 54f, continuationPaint)
        return 80f
      }
    }

    fun drawPageFooter(c: Canvas, pageNum: Int) {
      c.drawLine(leftMargin, 804f, rightMargin, 804f, linePaint)
      c.drawText(
        "Kalkulator Timbang Ayam Broiler • Dokumen Rekapitulasi Resmi",
        leftMargin,
        818f,
        footerPaint
      )
      val pageText = "Halaman $pageNum"
      val pageTextWidth = footerPaint.measureText(pageText)
      c.drawText(pageText, rightMargin - pageTextWidth, 818f, footerPaint)
    }

    fun checkAndStartNewPage(neededHeight: Float) {
      if (currentY + neededHeight > 785f) {
        drawPageFooter(canvas, pageNumber)
        document.finishPage(currentPage)
        pageNumber++
        currentPage = document.startPage(pageInfo)
        canvas = currentPage.canvas
        currentY = drawPageHeader(canvas, isFirstPage = false)
      }
    }

    // 1. Draw First Page Header
    currentY = drawPageHeader(canvas, isFirstPage = true)

    // 2. INFORMASI PENIMBANGAN
    canvas.drawText("INFORMASI PENIMBANGAN", leftMargin, currentY + 11f, sectionHeaderPaint)
    currentY += 16f

    val infoBoxTop = currentY
    val infoBoxHeight = 58f
    canvas.drawRoundRect(
      RectF(leftMargin, infoBoxTop, rightMargin, infoBoxTop + infoBoxHeight),
      6f,
      6f,
      grayBorderPaint
    )

    // Left Column Info (Kandang & Lokasi)
    val leftColX = leftMargin + 12f
    val line1Y = infoBoxTop + 16f
    val line2Y = infoBoxTop + 33f
    val line3Y = infoBoxTop + 49f

    canvas.drawText("Nama Kandang", leftColX, line1Y, labelPaint)
    canvas.drawText(": ${record.namaKandang}", leftColX + 70f, line1Y, valuePaint)

    canvas.drawText("Alamat Jalan", leftColX, line2Y, labelPaint)
    canvas.drawText(": ${record.alamatJalan}", leftColX + 70f, line2Y, valuePaint)

    canvas.drawText("Wilayah", leftColX, line3Y, labelPaint)
    canvas.drawText(
      ": Kec. ${record.kecamatan}, Kab. ${record.kabupaten}",
      leftColX + 70f,
      line3Y,
      valuePaint
    )

    // Right Column Info (Tanggal & Waktu)
    val rightColX = leftMargin + 320f
    canvas.drawText("Tanggal", rightColX, line1Y, labelPaint)
    canvas.drawText(": ${record.formattedDate}", rightColX + 45f, line1Y, valuePaint)

    canvas.drawText("Waktu", rightColX, line2Y, labelPaint)
    canvas.drawText(": ${record.formattedTime}", rightColX + 45f, line2Y, valuePaint)

    currentY = infoBoxTop + infoBoxHeight + 14f

    // 3. RINGKASAN HASIL
    canvas.drawText("RINGKASAN HASIL", leftMargin, currentY + 11f, sectionHeaderPaint)
    currentY += 16f

    val cardGap = 8f
    val cardWidth = (contentWidth - (cardGap * 2)) / 3f
    val cardHeight = 46f
    val cardTop = currentY

    // Card 1: Total Ekor
    val card1Left = leftMargin
    val card1Right = card1Left + cardWidth
    canvas.drawRoundRect(
      RectF(card1Left, cardTop, card1Right, cardTop + cardHeight),
      6f,
      6f,
      lightGreenFillPaint
    )
    canvas.drawRoundRect(
      RectF(card1Left, cardTop, card1Right, cardTop + cardHeight),
      6f,
      6f,
      cardBorderPaint
    )
    cardTitlePaint.color = Color.rgb(46, 125, 50)
    cardValuePaint.color = Color.rgb(27, 94, 32)
    val c1Title = "TOTAL EKOR"
    val c1Value = "${WeighingFormatter.formatEkor(record.totalEkor)}"
    canvas.drawText(
      c1Title,
      card1Left + (cardWidth - cardTitlePaint.measureText(c1Title)) / 2f,
      cardTop + 15f,
      cardTitlePaint
    )
    canvas.drawText(
      c1Value,
      card1Left + (cardWidth - cardValuePaint.measureText(c1Value)) / 2f,
      cardTop + 35f,
      cardValuePaint
    )

    // Card 2: Total Berat
    val card2Left = card1Right + cardGap
    val card2Right = card2Left + cardWidth
    canvas.drawRoundRect(
      RectF(card2Left, cardTop, card2Right, cardTop + cardHeight),
      6f,
      6f,
      lightGreenFillPaint
    )
    canvas.drawRoundRect(
      RectF(card2Left, cardTop, card2Right, cardTop + cardHeight),
      6f,
      6f,
      cardBorderPaint
    )
    val c2Title = "TOTAL BERAT"
    val c2Value = WeighingFormatter.formatKg(record.totalBeratKg)
    canvas.drawText(
      c2Title,
      card2Left + (cardWidth - cardTitlePaint.measureText(c2Title)) / 2f,
      cardTop + 15f,
      cardTitlePaint
    )
    canvas.drawText(
      c2Value,
      card2Left + (cardWidth - cardValuePaint.measureText(c2Value)) / 2f,
      cardTop + 35f,
      cardValuePaint
    )

    // Card 3: Bobot Rata-rata
    val card3Left = card2Right + cardGap
    val card3Right = card3Left + cardWidth
    canvas.drawRoundRect(
      RectF(card3Left, cardTop, card3Right, cardTop + cardHeight),
      6f,
      6f,
      lightAmberFillPaint
    )
    canvas.drawRoundRect(
      RectF(card3Left, cardTop, card3Right, cardTop + cardHeight),
      6f,
      6f,
      amberBorderPaint
    )
    cardTitlePaint.color = Color.rgb(230, 81, 0)
    cardValuePaint.color = Color.rgb(230, 81, 0)
    val c3Title = "BOBOT RATA-RATA"
    val c3Value = String.format(Locale.US, "%.3f kg", record.bobotRataRata)
    canvas.drawText(
      c3Title,
      card3Left + (cardWidth - cardTitlePaint.measureText(c3Title)) / 2f,
      cardTop + 15f,
      cardTitlePaint
    )
    canvas.drawText(
      c3Value,
      card3Left + (cardWidth - cardValuePaint.measureText(c3Value)) / 2f,
      cardTop + 35f,
      cardValuePaint
    )

    currentY = cardTop + cardHeight + 16f

    // 4. RINCIAN PENIMBANGAN PER SET
    canvas.drawText("RINCIAN PENIMBANGAN", leftMargin, currentY + 11f, sectionHeaderPaint)
    currentY += 16f

    // Column Widths for Table
    // NO: 50f, EKOR: 130f, BERAT (KG): 180f, RATA-RATA: 163f = 523f
    val colNoWidth = 50f
    val colEkorWidth = 130f
    val colBeratWidth = 180f
    val colAvgWidth = 163f

    val col1X = leftMargin
    val col2X = col1X + colNoWidth
    val col3X = col2X + colEkorWidth
    val col4X = col3X + colBeratWidth
    val tableRight = col4X + colAvgWidth

    sets.forEach { set ->
      // Prepare valid rows
      val rows = set.rows.filter { it.isValid || it.ekor.isNotBlank() || it.berat.isNotBlank() }

      // Estimate space for this set: Set Header (22) + Table Header (18) + (rows.size * 17) + Subtotal (20) + gap (12)
      val estimatedSetHeight = 22f + 18f + (maxOf(rows.size, 1) * 17f) + 20f + 12f
      checkAndStartNewPage(minOf(estimatedSetHeight, 80f))

      // Set Banner
      val setBannerHeight = 20f
      canvas.drawRoundRect(
        RectF(leftMargin, currentY, rightMargin, currentY + setBannerHeight),
        4f,
        4f,
        lightGreenFillPaint
      )
      canvas.drawRoundRect(
        RectF(leftMargin, currentY, rightMargin, currentY + setBannerHeight),
        4f,
        4f,
        cardBorderPaint
      )

      val setTitlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.rgb(27, 94, 32)
        textSize = 9.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }
      canvas.drawText("SET ${set.setNumber}", leftMargin + 8f, currentY + 14f, setTitlePaint)

      val setSummaryText =
        "Subtotal: ${set.totalEkor} ekor | ${WeighingFormatter.formatKg(set.totalBeratKg)} | Rata-rata: ${String.format(Locale.US, "%.3f", set.bobotRataRata)} kg"
      val setSummaryPaint = Paint().apply {
        isAntiAlias = true
        color = Color.rgb(60, 70, 80)
        textSize = 8.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }
      val summaryWidth = setSummaryPaint.measureText(setSummaryText)
      canvas.drawText(setSummaryText, rightMargin - summaryWidth - 8f, currentY + 14f, setSummaryPaint)

      currentY += setBannerHeight + 4f

      // Table Header Row
      val thHeight = 18f
      canvas.drawRect(leftMargin, currentY, tableRight, currentY + thHeight, greenBannerPaint)

      fun drawCentered(text: String, startX: Float, width: Float, y: Float, paint: Paint) {
        val tw = paint.measureText(text)
        canvas.drawText(text, startX + (width - tw) / 2f, y, paint)
      }

      drawCentered("NO", col1X, colNoWidth, currentY + 12.5f, tableHeaderPaint)
      drawCentered("EKOR", col2X, colEkorWidth, currentY + 12.5f, tableHeaderPaint)
      drawCentered("BERAT (KG)", col3X, colBeratWidth, currentY + 12.5f, tableHeaderPaint)
      drawCentered("RERATA (KG/EKOR)", col4X, colAvgWidth, currentY + 12.5f, tableHeaderPaint)

      currentY += thHeight

      // Table Rows
      if (rows.isEmpty()) {
        val emptyHeight = 18f
        canvas.drawRect(leftMargin, currentY, tableRight, currentY + emptyHeight, zebraFillPaint)
        canvas.drawLine(leftMargin, currentY + emptyHeight, tableRight, currentY + emptyHeight, linePaint)
        val emptyText = "Tidak ada baris timbangan di set ini"
        drawCentered(emptyText, leftMargin, contentWidth, currentY + 12.5f, labelPaint)
        currentY += emptyHeight
      } else {
        rows.forEachIndexed { index, row ->
          checkAndStartNewPage(18f)

          val rowHeight = 17f
          val isEven = (index % 2 == 0)
          if (!isEven) {
            canvas.drawRect(leftMargin, currentY, tableRight, currentY + rowHeight, zebraFillPaint)
          }

          // Bottom border for each row
          canvas.drawLine(leftMargin, currentY + rowHeight, tableRight, currentY + rowHeight, linePaint)

          // Values
          drawCentered("${row.no}", col1X, colNoWidth, currentY + 12f, tableCellPaint)
          drawCentered("${row.ekor} ekor", col2X, colEkorWidth, currentY + 12f, tableCellBoldPaint)
          drawCentered(
            WeighingFormatter.formatKg(row.beratDouble),
            col3X,
            colBeratWidth,
            currentY + 12f,
            tableCellBoldPaint
          )

          val rowAvg = if (row.ekorInt > 0) row.beratDouble / row.ekorInt else 0.0
          val rowAvgText = String.format(Locale.US, "%.3f kg", rowAvg)
          drawCentered(rowAvgText, col4X, colAvgWidth, currentY + 12f, tableCellPaint)

          currentY += rowHeight
        }
      }

      // Subtotal Row for the set
      checkAndStartNewPage(20f)
      val subtotalHeight = 19f
      canvas.drawRect(leftMargin, currentY, tableRight, currentY + subtotalHeight, lightGreenFillPaint)
      canvas.drawLine(leftMargin, currentY + subtotalHeight, tableRight, currentY + subtotalHeight, cardBorderPaint)

      val subtotalLabelPaint = Paint().apply {
        isAntiAlias = true
        color = Color.rgb(27, 94, 32)
        textSize = 8.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }

      drawCentered("Subtotal", col1X, colNoWidth, currentY + 13f, subtotalLabelPaint)
      drawCentered("${set.totalEkor} ekor", col2X, colEkorWidth, currentY + 13f, subtotalLabelPaint)
      drawCentered(
        WeighingFormatter.formatKg(set.totalBeratKg),
        col3X,
        colBeratWidth,
        currentY + 13f,
        subtotalLabelPaint
      )
      val setAvgText = String.format(Locale.US, "%.3f kg", set.bobotRataRata)
      drawCentered(setAvgText, col4X, colAvgWidth, currentY + 13f, subtotalLabelPaint)

      // Outer table border
      canvas.drawRect(leftMargin, currentY - (rows.size * 17f) - thHeight, tableRight, currentY + subtotalHeight, grayBorderPaint)

      currentY += subtotalHeight + 12f
    }

    // 5. TOTAL KESELURUHAN
    checkAndStartNewPage(65f)
    val grandTotalHeight = 56f
    val grandTotalTop = currentY
    canvas.drawRoundRect(
      RectF(leftMargin, grandTotalTop, rightMargin, grandTotalTop + grandTotalHeight),
      6f,
      6f,
      lightGreenFillPaint
    )
    canvas.drawRoundRect(
      RectF(leftMargin, grandTotalTop, rightMargin, grandTotalTop + grandTotalHeight),
      6f,
      6f,
      cardBorderPaint
    )

    val grandTitlePaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(27, 94, 32)
      textSize = 10f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val grandMetricValPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(27, 94, 32)
      textSize = 9.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("TOTAL KESELURUHAN", leftMargin + 12f, grandTotalTop + 16f, grandTitlePaint)
    canvas.drawLine(leftMargin + 12f, grandTotalTop + 22f, rightMargin - 12f, grandTotalTop + 22f, cardBorderPaint)

    val colMetricsY = grandTotalTop + 40f
    val metricWidth = (contentWidth - 24f) / 3f

    // 1. Total Ekor
    val m1X = leftMargin + 12f
    canvas.drawText("Jumlah Ekor", m1X, colMetricsY - 5f, labelPaint)
    canvas.drawText("${record.totalEkor} ekor", m1X, colMetricsY + 10f, grandMetricValPaint)

    // 2. Total Berat
    val m2X = m1X + metricWidth
    canvas.drawText("Total Berat", m2X, colMetricsY - 5f, labelPaint)
    canvas.drawText(WeighingFormatter.formatKg(record.totalBeratKg), m2X, colMetricsY + 10f, grandMetricValPaint)

    // 3. Bobot Rata-rata
    val m3X = m2X + metricWidth
    val grandAvgPaint = Paint().apply {
      isAntiAlias = true
      color = Color.rgb(230, 81, 0)
      textSize = 9.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("Bobot Rata-rata", m3X, colMetricsY - 5f, labelPaint)
    canvas.drawText(
      String.format(Locale.US, "%.3f kg/ekor", record.bobotRataRata),
      m3X,
      colMetricsY + 10f,
      grandAvgPaint
    )

    // Finish last page
    drawPageFooter(canvas, pageNumber)
    document.finishPage(currentPage)

    return document
  }

  /**
   * Generates and writes the PDF file to application cache directory.
   */
  fun generatePdfFileToCache(
    context: Context,
    record: WeighingRecord,
    sets: List<WeighingSet>
  ): File {
    val fileName = getSanitizedFileName(record.namaKandang, record.formattedDate)
    val pdfDir = File(context.cacheDir, "pdfs").apply { if (!exists()) mkdirs() }
    val pdfFile = File(pdfDir, fileName)

    val doc = createPdfDocument(record, sets)
    FileOutputStream(pdfFile).use { out ->
      doc.writeTo(out)
    }
    doc.close()
    return pdfFile
  }

  /**
   * Action 1: DOWNLOAD PDF
   * Saves the PDF to the device's local storage (Downloads folder).
   * Supports Android 10+ (Scoped Storage / MediaStore) and older versions gracefully.
   * 100% offline, no server, no Firebase, no internet.
   */
  fun downloadPdf(
    context: Context,
    record: WeighingRecord,
    sets: List<WeighingSet>
  ): Pair<Boolean, String> {
    return try {
      val fileName = getSanitizedFileName(record.namaKandang, record.formattedDate)
      val doc = createPdfDocument(record, sets)

      var savedSuccessfully = false
      var displayLocation = "folder Download"

      // MediaStore for Android 10 (Q) and above
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
          put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
          resolver.openOutputStream(uri)?.use { outputStream ->
            doc.writeTo(outputStream)
            savedSuccessfully = true
            displayLocation = "Download/$fileName"
          }
        }
      }

      // Fallback or Android 9 and below
      if (!savedSuccessfully) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()
        val destFile = File(downloadsDir, fileName)
        FileOutputStream(destFile).use { out ->
          doc.writeTo(out)
          savedSuccessfully = true
          displayLocation = destFile.absolutePath
        }
      }

      // Also ensure a copy is saved in app filesDir for instant access/opening
      val localDir = File(context.filesDir, "pdfs").apply { if (!exists()) mkdirs() }
      val localCopy = File(localDir, fileName)
      FileOutputStream(localCopy).use { out ->
        doc.writeTo(out)
      }

      doc.close()

      if (savedSuccessfully) {
        Pair(true, "PDF berhasil disimpan ke $displayLocation")
      } else {
        Pair(false, "Gagal menyimpan file PDF")
      }
    } catch (e: Exception) {
      Pair(false, "Gagal mengunduh PDF: ${e.localizedMessage ?: "Terjadi kesalahan"}")
    }
  }

  /**
   * Action 2: BAGIKAN PDF
   * Uses Android system sharing (Share Sheet) to share the PDF via WhatsApp,
   * Gmail, Telegram, Bluetooth, Drive, etc.
   * Uses secure FileProvider content URI (no unsafe file:// URI).
   */
  fun sharePdf(
    context: Context,
    record: WeighingRecord,
    sets: List<WeighingSet>
  ) {
    try {
      val pdfFile = generatePdfFileToCache(context, record, sets)
      val authority = "${context.packageName}.fileprovider"
      val contentUri = FileProvider.getUriForFile(context, authority, pdfFile)

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "Laporan Timbang Broiler - ${record.namaKandang}")
        putExtra(
          Intent.EXTRA_TEXT,
          "Laporan Rekapitulasi Penimbangan Broiler:\n" +
            "Kandang: ${record.namaKandang}\n" +
            "Tanggal: ${record.formattedDate} (${record.formattedTime})\n" +
            "Total: ${record.totalEkor} ekor | ${WeighingFormatter.formatKg(record.totalBeratKg)}\n" +
            "Rata-rata: ${String.format(Locale.US, "%.3f", record.bobotRataRata)} kg/ekor"
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(shareIntent, "Bagikan PDF Rekap Timbang").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
    } catch (e: Exception) {
      Toast.makeText(
        context,
        "Gagal membagikan PDF: ${e.localizedMessage ?: "Terjadi kesalahan"}",
        Toast.LENGTH_LONG
      ).show()
    }
  }
}
