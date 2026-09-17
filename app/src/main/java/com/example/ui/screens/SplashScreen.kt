package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GreenDarkText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextDarkSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit
) {
  val alphaAnim = remember { Animatable(0f) }
  val scaleAnim = remember { Animatable(0.92f) }

  LaunchedEffect(Unit) {
    // Smooth entry animation
    alphaAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    scaleAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    // Hold briefly for clean readability and transition
    delay(1200)
    onSplashFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(PureWhite)
      .testTag("splash_screen_container"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = 32.dp)
        .alpha(alphaAnim.value)
        .scale(scaleAnim.value),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. App Icon with clean elevation & rounded corners
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .size(108.dp)
          .testTag("splash_app_icon")
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_timbang_broiler),
          contentDescription = "Logo FMC TIMBANG BROILER",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(26.dp))
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 2. Branding Title
      Text(
        text = "TIMBANG BROILER",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = GreenDarkText,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("splash_title")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Subtitle / Tagline
      Text(
        text = "Alat pencatatan dan perhitungan hasil penimbangan ayam broiler",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = TextDarkSecondary,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
        modifier = Modifier
          .widthIn(max = 300.dp)
          .testTag("splash_subtitle")
      )
    }
  }
}
