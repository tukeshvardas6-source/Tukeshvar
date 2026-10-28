package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartoonVideo
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
  video: CartoonVideo,
  viewModel: MainViewModel,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.closeVideoPlayer()
    onClose()
  }

  val context = LocalContext.current
  val currentUser by viewModel.currentUser.collectAsState()

  var currentSceneIndex by remember { mutableStateOf(0) }
  var isPlaying by remember { mutableStateOf(true) }
  var downloadToastVisible by remember { mutableStateOf(false) }

  val scenes = video.scenes
  val currentScene = scenes.getOrNull(currentSceneIndex) ?: scenes.firstOrNull()

  // Auto progression timer when playing
  LaunchedEffect(isPlaying, currentSceneIndex) {
    if (isPlaying && scenes.isNotEmpty()) {
      // Speak the dialogue for current scene
      currentScene?.let { scene ->
        val isHindi = video.language.contains("Hindi", ignoreCase = true)
        val textToSpeak = if (isHindi) scene.dialogueHindi else scene.dialogueEnglish
        viewModel.speechSynthesizer.speakNarration(textToSpeak, isHindi)
      }

      val sceneDurationMs = (video.durationSec * 1000L / scenes.size).coerceAtLeast(3000L)
      delay(sceneDurationMs)

      if (currentSceneIndex < scenes.size - 1) {
        currentSceneIndex++
      } else {
        isPlaying = false // Finished playing all scenes
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("video_player_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top Controls Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = {
              viewModel.closeVideoPlayer()
              onClose()
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
              .testTag("btn_close_player")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Player",
              tint = Color.White
            )
          }

          Column {
            Text(
              text = video.title,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              maxLines = 1
            )
            Text(
              text = "${video.style} • ${video.character} • ${video.durationSec}s",
              color = TdYellowBright,
              fontSize = 11.sp
            )
          }
        }

        Surface(
          color = TdPurpleSecondary.copy(alpha = 0.8f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "TD Mahant AI",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Animated Scene Canvas Stage
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(
                TdBackgroundDark,
                TdPurpleDark,
                TdBlueDark
              )
            )
          )
          .border(2.dp, TdYellowBright.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        // Decorative Canvas Background Stars & Sparkles
        Canvas(modifier = Modifier.fillMaxSize()) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          // Draw playful decorative cartoon sparkles
          drawCircle(
            color = Color(0x33F59E0B),
            radius = 120f,
            center = Offset(canvasWidth * 0.2f, canvasHeight * 0.3f)
          )
          drawCircle(
            color = Color(0x337C3AED),
            radius = 160f,
            center = Offset(canvasWidth * 0.8f, canvasHeight * 0.7f)
          )
        }

        // Active Scene Card Content
        currentScene?.let { scene ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Scene Header Tag
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                color = TdBluePrimary.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Scene ${scene.sceneNumber} of ${scenes.size}",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Surface(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = scene.backgroundVibe,
                  color = TdYellowGold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            // Big Character / Mood Animation Centerpiece
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(110.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      listOf(TdYellowBright.copy(alpha = 0.4f), Color.Transparent)
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = scene.moodEmoji,
                  fontSize = 62.sp
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = scene.characterAction,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
              )
            }

            // Dialogue Speech Bubble
            Surface(
              modifier = Modifier.fillMaxWidth(),
              color = Color.Black.copy(alpha = 0.75f),
              shape = RoundedCornerShape(16.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, TdYellowBright.copy(alpha = 0.5f))
            ) {
              Column(
                modifier = Modifier.padding(14.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "💬 ${video.character}:",
                    color = TdYellowBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )

                  // Voice Play Button
                  IconButton(
                    onClick = {
                      val isHindi = video.language.contains("Hindi", ignoreCase = true)
                      val text = if (isHindi) scene.dialogueHindi else scene.dialogueEnglish
                      viewModel.speechSynthesizer.speakNarration(text, isHindi)
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.VolumeUp,
                      contentDescription = "Speak line",
                      tint = TdYellowBright,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = scene.dialogueHindi,
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Medium
                )

                if (scene.dialogueEnglish.isNotBlank() && scene.dialogueEnglish != scene.dialogueHindi) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Sub: \"${scene.dialogueEnglish}\"",
                    color = TdTextSecondaryDark,
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                  )
                }
              }
            }
          }
        }
      }

      // Timeline & Scene Indicator Dots
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        scenes.forEachIndexed { index, _ ->
          val isCurrent = index == currentSceneIndex
          Box(
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .height(6.dp)
              .width(if (isCurrent) 28.dp else 10.dp)
              .clip(CircleShape)
              .background(if (isCurrent) TdYellowBright else Color.White.copy(alpha = 0.3f))
          )
        }
      }

      // Player Control Bar
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Prev Scene
          IconButton(
            onClick = {
              if (currentSceneIndex > 0) currentSceneIndex--
            },
            enabled = currentSceneIndex > 0
          ) {
            Icon(
              imageVector = Icons.Default.SkipPrevious,
              contentDescription = "Previous Scene",
              tint = if (currentSceneIndex > 0) Color.White else TdTextSecondaryDark
            )
          }

          // Play / Pause
          IconButton(
            onClick = {
              if (currentSceneIndex >= scenes.size - 1 && !isPlaying) {
                currentSceneIndex = 0
              }
              isPlaying = !isPlaying
            },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(TdYellowBright)
              .testTag("btn_play_pause")
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pause" else "Play",
              tint = Color.Black,
              modifier = Modifier.size(28.dp)
            )
          }

          // Next Scene
          IconButton(
            onClick = {
              if (currentSceneIndex < scenes.size - 1) currentSceneIndex++
            },
            enabled = currentSceneIndex < scenes.size - 1
          ) {
            Icon(
              imageVector = Icons.Default.SkipNext,
              contentDescription = "Next Scene",
              tint = if (currentSceneIndex < scenes.size - 1) Color.White else TdTextSecondaryDark
            )
          }

          // Download Video
          IconButton(
            onClick = {
              downloadToastVisible = true
            },
            modifier = Modifier.testTag("btn_download_video")
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Download Video",
              tint = TdBlueLight
            )
          }

          // Share Video
          IconButton(
            onClick = {
              val shareText = """
                🎬 Watch my new AI Cartoon Video: '${video.title}'!
                Created on TD Mahant Cartoon Videos app.
                Use my referral code: ${currentUser?.referralCode ?: "TDM-9399"} to get 200 FREE coins!
                Download: https://tdmahant.app/watch/${video.id}
              """.trimIndent()

              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "Share TD Mahant Cartoon Video"))
            },
            modifier = Modifier.testTag("btn_share_video")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share Video",
              tint = TdPurpleLight
            )
          }
        }
      }

      // Download Toast Confirmation
      if (downloadToastVisible) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
          color = TdSuccessGreen,
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "✅ Cartoon Video saved to device gallery!",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { downloadToastVisible = false }) {
              Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
