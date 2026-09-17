package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.BerandaScreen
import com.example.ui.screens.DataTersimpanScreen
import com.example.ui.screens.PengaturanScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TimbangScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WeighingViewModel
import kotlinx.coroutines.flow.collectLatest

enum class Screen {
  SPLASH,
  BERANDA,
  TIMBANG,
  DATA_TERSIMPAN,
  PENGATURAN
}

class MainActivity : ComponentActivity() {
  private val viewModel: WeighingViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        MainApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainApp(viewModel: WeighingViewModel) {
  var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Listen to ViewModel toast/snackbar messages
  LaunchedEffect(Unit) {
    viewModel.toastMessage.collectLatest { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  // Handle system back button
  BackHandler(enabled = currentScreen != Screen.BERANDA && currentScreen != Screen.SPLASH) {
    currentScreen = Screen.BERANDA
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    AnimatedContent(
      targetState = currentScreen,
      transitionSpec = {
        if (initialState == Screen.SPLASH) {
          fadeIn(animationSpec = tween(500)).togetherWith(fadeOut(animationSpec = tween(400)))
        } else if (targetState != Screen.BERANDA && initialState == Screen.BERANDA) {
          (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
            slideOutHorizontally { width -> -width } + fadeOut()
          )
        } else {
          (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
            slideOutHorizontally { width -> width } + fadeOut()
          )
        }
      },
      label = "ScreenTransition",
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) { screen ->
      when (screen) {
        Screen.SPLASH -> {
          SplashScreen(
            onSplashFinished = { currentScreen = Screen.BERANDA }
          )
        }

        Screen.BERANDA -> {
          BerandaScreen(
            viewModel = viewModel,
            onMulaiMenimbang = { currentScreen = Screen.TIMBANG },
            onDataTersimpan = { currentScreen = Screen.DATA_TERSIMPAN },
            onPengaturan = { currentScreen = Screen.PENGATURAN }
          )
        }

        Screen.TIMBANG -> {
          TimbangScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = Screen.BERANDA }
          )
        }

        Screen.DATA_TERSIMPAN -> {
          DataTersimpanScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = Screen.BERANDA },
            onMulaiMenimbang = { currentScreen = Screen.TIMBANG }
          )
        }

        Screen.PENGATURAN -> {
          PengaturanScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = Screen.BERANDA }
          )
        }
      }
    }
  }
}
