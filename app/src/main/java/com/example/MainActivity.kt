package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatusFeedbackBanner
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent()
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val playingVideo by viewModel.playingVideo.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()
  val successMessage by viewModel.successMessage.collectAsState()

  // Handle system back navigation
  BackHandler(enabled = currentScreen != AppScreen.STUDIO || playingVideo != null) {
    if (playingVideo != null) {
      viewModel.closeVideoPlayer()
    } else {
      viewModel.navigateTo(AppScreen.STUDIO)
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        if (playingVideo == null) {
          AppTopBar(
            currentScreen = currentScreen,
            coinBalance = currentUser?.coinBalance ?: 0,
            userName = currentUser?.name,
            onNavigateToWallet = { viewModel.navigateTo(AppScreen.WALLET) },
            onNavigateToAuth = { viewModel.navigateTo(AppScreen.AUTH) },
            onNavigateToPlayStore = { viewModel.navigateTo(AppScreen.PLAY_STORE_INFO) }
          )
        }
      },
      bottomBar = {
        if (playingVideo == null && currentScreen != AppScreen.PLAY_STORE_INFO && currentScreen != AppScreen.ADMIN_PANEL) {
          AppBottomNavBar(
            currentScreen = currentScreen,
            onNavigate = { screen -> viewModel.navigateTo(screen) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
          when (screen) {
            AppScreen.STUDIO -> {
              StudioScreen(
                viewModel = viewModel,
                onOpenVideoPlayer = {
                  // Triggered when video is generated
                }
              )
            }

            AppScreen.MY_VIDEOS -> {
              MyVideosScreen(
                viewModel = viewModel,
                onOpenVideo = { video -> viewModel.openVideoPlayer(video) },
                onNavigateToStudio = { viewModel.navigateTo(AppScreen.STUDIO) }
              )
            }

            AppScreen.WALLET -> {
              WalletScreen(
                viewModel = viewModel,
                onNavigateToReferral = { viewModel.navigateTo(AppScreen.REFERRAL) }
              )
            }

            AppScreen.REFERRAL -> {
              ReferralScreen(
                viewModel = viewModel
              )
            }

            AppScreen.OWNER_EARNINGS -> {
              OwnerEarningsScreen(
                viewModel = viewModel,
                onNavigateToAdmin = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) }
              )
            }

            AppScreen.ADMIN_PANEL -> {
              AdminDashboardScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(AppScreen.OWNER_EARNINGS) }
              )
            }

            AppScreen.AUTH -> {
              AuthScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(AppScreen.STUDIO) }
              )
            }

            AppScreen.PLAY_STORE_INFO -> {
              PlayStoreListingScreen(
                onBack = { viewModel.navigateTo(AppScreen.STUDIO) }
              )
            }
          }
        }

        // Status Feedback Banner (Snackbar replacement)
        StatusFeedbackBanner(
          errorMessage = errorMessage,
          successMessage = successMessage,
          onDismiss = { viewModel.clearMessages() }
        )
      }
    }

    // Fullscreen Cartoon Video Player Modal
    playingVideo?.let { video ->
      VideoPlayerScreen(
        video = video,
        viewModel = viewModel,
        onClose = { viewModel.closeVideoPlayer() }
      )
    }
  }
}
