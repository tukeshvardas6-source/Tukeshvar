package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
  currentScreen: AppScreen,
  coinBalance: Int,
  userName: String?,
  onNavigateToWallet: () -> Unit,
  onNavigateToAuth: () -> Unit,
  onNavigateToPlayStore: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier.testTag("app_top_bar"),
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = TdBackgroundDark,
      titleContentColor = Color.White
    ),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // App Logo
        Image(
          painter = painterResource(id = R.drawable.ic_td_mahant_logo),
          contentDescription = "TD Mahant Logo",
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp)),
          contentScale = ContentScale.Crop
        )

        Column {
          Text(
            text = "TD Mahant",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "Cartoon Videos",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TdYellowBright
          )
        }
      }
    },
    actions = {
      // Coin Balance Badge
      CoinBadge(
        coins = coinBalance,
        onClick = onNavigateToWallet,
        modifier = Modifier.padding(end = 6.dp)
      )

      // Play Store & Policy Icon
      IconButton(
        onClick = onNavigateToPlayStore,
        modifier = Modifier.testTag("btn_store_policy")
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "App Info & Policy",
          tint = TdPurpleLight
        )
      }

      // Profile / Auth
      IconButton(
        onClick = onNavigateToAuth,
        modifier = Modifier.testTag("btn_auth_profile")
      ) {
        Icon(
          imageVector = if (userName != null) Icons.Default.AccountCircle else Icons.Outlined.Person,
          contentDescription = "User Account",
          tint = if (userName != null) TdYellowBright else Color.White
        )
      }
    }
  )
}

@Composable
fun CoinBadge(
  coins: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .testTag("coin_badge"),
    color = TdYellowAccent.copy(alpha = 0.18f),
    shape = RoundedCornerShape(20.dp),
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(TdYellowBright, TdYellowDark))),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "🪙",
          fontSize = 11.sp
        )
      }
      Text(
        text = "$coins",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = TdYellowBright
      )
    }
  }
}

@Composable
fun AppBottomNavBar(
  currentScreen: AppScreen,
  onNavigate: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier
      .windowInsetsPadding(WindowInsets.navigationBars)
      .testTag("bottom_nav_bar"),
    containerColor = TdSurfaceDark,
    contentColor = Color.White
  ) {
    NavigationBarItem(
      selected = currentScreen == AppScreen.STUDIO,
      onClick = { onNavigate(AppScreen.STUDIO) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.STUDIO) Icons.Filled.MovieCreation else Icons.Outlined.MovieCreation,
          contentDescription = "Studio"
        )
      },
      label = { Text("Studio", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = TdYellowBright,
        selectedTextColor = TdYellowBright,
        indicatorColor = TdBluePrimary.copy(alpha = 0.35f),
        unselectedIconColor = TdTextSecondaryDark,
        unselectedTextColor = TdTextSecondaryDark
      )
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.MY_VIDEOS,
      onClick = { onNavigate(AppScreen.MY_VIDEOS) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.MY_VIDEOS) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
          contentDescription = "My Videos"
        )
      },
      label = { Text("My Videos", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = TdYellowBright,
        selectedTextColor = TdYellowBright,
        indicatorColor = TdBluePrimary.copy(alpha = 0.35f),
        unselectedIconColor = TdTextSecondaryDark,
        unselectedTextColor = TdTextSecondaryDark
      )
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.WALLET,
      onClick = { onNavigate(AppScreen.WALLET) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.WALLET) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
          contentDescription = "Wallet"
        )
      },
      label = { Text("Wallet", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = TdYellowBright,
        selectedTextColor = TdYellowBright,
        indicatorColor = TdBluePrimary.copy(alpha = 0.35f),
        unselectedIconColor = TdTextSecondaryDark,
        unselectedTextColor = TdTextSecondaryDark
      )
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.REFERRAL,
      onClick = { onNavigate(AppScreen.REFERRAL) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.REFERRAL) Icons.Filled.CardGiftcard else Icons.Outlined.CardGiftcard,
          contentDescription = "Referral"
        )
      },
      label = { Text("Referral", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = TdYellowBright,
        selectedTextColor = TdYellowBright,
        indicatorColor = TdBluePrimary.copy(alpha = 0.35f),
        unselectedIconColor = TdTextSecondaryDark,
        unselectedTextColor = TdTextSecondaryDark
      )
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.OWNER_EARNINGS || currentScreen == AppScreen.ADMIN_PANEL,
      onClick = { onNavigate(AppScreen.OWNER_EARNINGS) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.OWNER_EARNINGS) Icons.Filled.CurrencyRupee else Icons.Outlined.CurrencyRupee,
          contentDescription = "Owner"
        )
      },
      label = { Text("Owner", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = TdYellowBright,
        selectedTextColor = TdYellowBright,
        indicatorColor = TdPurpleSecondary.copy(alpha = 0.35f),
        unselectedIconColor = TdTextSecondaryDark,
        unselectedTextColor = TdTextSecondaryDark
      )
    )
  }
}

@Composable
fun StatusFeedbackBanner(
  errorMessage: String?,
  successMessage: String?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = errorMessage != null || successMessage != null,
    modifier = modifier
  ) {
    val isError = errorMessage != null
    val msg = errorMessage ?: successMessage ?: ""

    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .testTag("status_feedback_banner"),
      color = if (isError) TdErrorRed.copy(alpha = 0.95f) else TdSuccessGreen.copy(alpha = 0.95f),
      shape = RoundedCornerShape(12.dp),
      shadowElevation = 4.dp
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = if (isError) Icons.Default.Warning else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = msg,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
