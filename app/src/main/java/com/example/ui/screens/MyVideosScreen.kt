package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartoonVideo
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyVideosScreen(
  viewModel: MainViewModel,
  onOpenVideo: (CartoonVideo) -> Unit,
  onNavigateToStudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val videos by viewModel.videos.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var selectedFilter by remember { mutableStateOf("All") }
  val filters = listOf("All", "3D Animated", "Classic 2D Cartoon", "Anime Toon", "Claymation")

  val filteredVideos = if (selectedFilter == "All") {
    videos
  } else {
    videos.filter { it.style.contains(selectedFilter, ignoreCase = true) }
  }

  val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .padding(horizontal = 16.dp)
      .testTag("my_videos_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "My Cartoon Videos",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "${videos.size} videos generated",
          fontSize = 12.sp,
          color = TdYellowBright
        )
      }

      Button(
        onClick = onNavigateToStudio,
        colors = ButtonDefaults.buttonColors(
          containerColor = TdYellowBright,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("New Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Filter Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(filters) { filter ->
        val isSelected = selectedFilter == filter
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { selectedFilter = filter },
          color = if (isSelected) TdBluePrimary else TdSurfaceDark,
          shape = RoundedCornerShape(16.dp)
        ) {
          Text(
            text = filter,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TdTextSecondaryDark,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (filteredVideos.isEmpty()) {
      // Empty State
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(TdSurfaceDark),
            contentAlignment = Alignment.Center
          ) {
            Text("🎬", fontSize = 36.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "No cartoon videos yet",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Create your first cartoon video with TD Mahant AI! You have ${currentUser?.coinBalance ?: 0} coins ready to use.",
            fontSize = 12.sp,
            color = TdTextSecondaryDark,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onNavigateToStudio,
            colors = ButtonDefaults.buttonColors(
              containerColor = TdYellowBright,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Start Creating (10 Coins)", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
      ) {
        items(filteredVideos, key = { it.id }) { video ->
          VideoCardItem(
            video = video,
            dateStr = dateFormat.format(Date(video.createdAt)),
            onPlay = { onOpenVideo(video) },
            onShare = {
              val shareText = """
                🎬 Watch my AI Cartoon Video: '${video.title}'!
                Created on TD Mahant Cartoon Videos app.
                Use my code: ${currentUser?.referralCode ?: "TDM-9399"} to get 200 FREE coins!
                Download: https://tdmahant.app/watch/${video.id}
              """.trimIndent()
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "Share TD Mahant Video"))
            },
            onDelete = {
              viewModel.repository.deleteVideo(video.id)
            }
          )
        }
      }
    }
  }
}

@Composable
fun VideoCardItem(
  video: CartoonVideo,
  dateStr: String,
  onPlay: () -> Unit,
  onShare: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showDeleteDialog by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onPlay)
      .testTag("video_card_${video.id}"),
    colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column {
      // Thumbnail preview stage
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .background(
            Brush.horizontalGradient(
              listOf(TdPurpleDark, TdBlueDark)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = video.scenes.firstOrNull()?.moodEmoji ?: "🎬",
            fontSize = 40.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "▶ Tap to Watch",
              color = TdYellowBright,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        // Duration Tag
        Surface(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp),
          color = Color.Black.copy(alpha = 0.8f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "${video.durationSec}s • ${video.scenes.size} Scenes",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Details
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = video.title,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "\"${video.prompt}\"",
          fontSize = 12.sp,
          color = TdTextSecondaryDark,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Badges
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = TdBluePrimary.copy(alpha = 0.25f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = video.character,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = TdBlueLight,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Surface(
            color = TdPurpleSecondary.copy(alpha = 0.25f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = video.style,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = TdPurpleLight,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Surface(
            color = TdYellowAccent.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = video.language,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = TdYellowBright,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Footer Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = dateStr,
            fontSize = 11.sp,
            color = TdTextSecondaryDark
          )

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
              onClick = onShare,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = TdYellowBright,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = { showDeleteDialog = true },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = TdErrorRed,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Delete Cartoon Video?", color = Color.White) },
      text = { Text("Are you sure you want to delete '${video.title}'?", color = TdTextSecondaryDark) },
      confirmButton = {
        TextButton(onClick = {
          onDelete()
          showDeleteDialog = false
        }) {
          Text("Delete", color = TdErrorRed, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel", color = Color.White)
        }
      },
      containerColor = TdSurfaceElevatedDark
    )
  }
}
