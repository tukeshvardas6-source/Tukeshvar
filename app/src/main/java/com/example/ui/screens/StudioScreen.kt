package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.VideoStatus
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
  viewModel: MainViewModel,
  onOpenVideoPlayer: () -> Unit,
  modifier: Modifier = Modifier
) {
  val prompt by viewModel.promptInput.collectAsState()
  val selectedLanguage by viewModel.selectedLanguage.collectAsState()
  val selectedStyle by viewModel.selectedStyle.collectAsState()
  val selectedCharacter by viewModel.selectedCharacter.collectAsState()
  val selectedDuration by viewModel.selectedDuration.collectAsState()
  val isGenerating by viewModel.isGenerating.collectAsState()
  val generationStage by viewModel.generationStage.collectAsState()
  val generationMessage by viewModel.generationMessage.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  val scrollState = rememberScrollState()

  val promptSuggestions = listOf(
    "शेर और चिंटू का जादुई स्कूल (Magic school adventure)",
    "Space Monkey building a rocket to Mars",
    "जादुई बादल और टॉफ़ी की बारिश (Candy cloud rain)",
    "Superhero Kid saving the playful puppies",
    "रोबोट और तोते की अनोखी दोस्ती (Robot & Parrot friendship)"
  )

  val languages = listOf("Hindi", "English", "Hinglish")
  val styles = listOf("3D Animated", "Classic 2D Cartoon", "Anime Toon", "Claymation", "Comic Book")
  val characters = listOf(
    "Sheru the Lion" to "🦁",
    "Raju & Chintu" to "👦🧒",
    "Superhero Kid" to "🦸",
    "Princess Aanya" to "👑",
    "Detective Bhalu" to "🐻",
    "Magic Robot" to "🤖"
  )
  val durations = listOf(5, 10, 15)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(bottom = 90.dp)
    ) {
      // Hero Banner Box with gradient overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_studio_hero_banner),
          contentDescription = "Studio Hero Banner",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color.Transparent,
                  TdBackgroundDark.copy(alpha = 0.5f),
                  TdBackgroundDark
                )
              )
            )
        )

        // Banner Text Overlay
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Surface(
            color = TdPurpleSecondary.copy(alpha = 0.85f),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text(
              text = "AI CARTOON ENGINE 2.0",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Create Cartoon Videos in Seconds",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
          Text(
            text = "Enter story in Hindi or English • 10 Coins per Video",
            fontSize = 12.sp,
            color = TdYellowBright
          )
        }
      }

      // Coin & Promo Ribbon
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(TdYellowAccent.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text("🎁", fontSize = 16.sp)
            }
            Column {
              Text(
                text = "Welcome Bonus: 200 Free Coins",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
              )
              Text(
                text = "Wallet: ${currentUser?.coinBalance ?: 0} Coins available",
                fontSize = 11.sp,
                color = TdYellowBright
              )
            }
          }

          Surface(
            color = TdBluePrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "Cost: 10 🪙",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TdBlueLight,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Prompt Input Section
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TdYellowBright,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Story or Cartoon Prompt",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = prompt,
            onValueChange = { viewModel.promptInput.value = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("prompt_input"),
            placeholder = {
              Text(
                "वर्णन करें: आपका कार्टून वीडियो किस बारे में है? Describe your cartoon scene...",
                color = TdTextSecondaryDark,
                fontSize = 13.sp
              )
            },
            minLines = 3,
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TdPurpleSecondary,
              unfocusedBorderColor = TdSurfaceVariantDark,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              cursorColor = TdYellowBright
            ),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Try these story ideas:",
            fontSize = 11.sp,
            color = TdTextSecondaryDark
          )

          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(promptSuggestions) { idea ->
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .clickable { viewModel.promptInput.value = idea },
                color = TdSurfaceVariantDark,
                shape = RoundedCornerShape(16.dp)
              ) {
                Text(
                  text = idea,
                  fontSize = 11.sp,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      // Character Selection
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Choose Main Character",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(characters) { (name, emoji) ->
              val isSelected = selectedCharacter == name
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { viewModel.selectedCharacter.value = name }
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) TdYellowBright else TdSurfaceVariantDark,
                    shape = RoundedCornerShape(12.dp)
                  )
                  .testTag("char_select_$name"),
                color = if (isSelected) TdPurpleDark.copy(alpha = 0.5f) else TdSurfaceVariantDark,
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(text = emoji, fontSize = 26.sp)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) TdYellowBright else Color.White
                  )
                }
              }
            }
          }
        }
      }

      // Style & Language & Duration Row
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Style
          Text(
            text = "Animation Style",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(8.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(styles) { style ->
              val isSelected = selectedStyle == style
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.selectedStyle.value = style },
                label = { Text(style, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TdBluePrimary,
                  selectedLabelColor = Color.White,
                  containerColor = TdSurfaceVariantDark,
                  labelColor = TdTextSecondaryDark
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Language & Duration side-by-side
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Language
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Narration Language",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                languages.forEach { lang ->
                  val isSelected = selectedLanguage == lang
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { viewModel.selectedLanguage.value = lang }
                      .border(
                        width = 1.dp,
                        color = if (isSelected) TdYellowBright else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                      ),
                    color = if (isSelected) TdYellowDark else TdSurfaceVariantDark,
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = lang,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                  }
                }
              }
            }

            // Duration
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Video Duration",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                durations.forEach { sec ->
                  val isSelected = selectedDuration == sec
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { viewModel.selectedDuration.value = sec }
                      .border(
                        width = 1.dp,
                        color = if (isSelected) TdPurpleLight else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                      ),
                    color = if (isSelected) TdPurpleSecondary else TdSurfaceVariantDark,
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = "${sec}s",
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Generate Video CTA Button
      Button(
        onClick = {
          viewModel.generateVideo()
        },
        enabled = !isGenerating,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .padding(horizontal = 16.dp)
          .testTag("btn_generate_video"),
        colors = ButtonDefaults.buttonColors(
          containerColor = TdYellowBright,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayCircleFilled,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "Generate Cartoon Video",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
          )
          Surface(
            color = Color.Black.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "10 Coins",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.VerifiedUser,
          contentDescription = null,
          tint = TdSuccessGreen,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Zero Risk: Coins are NEVER deducted if generation fails.",
          fontSize = 11.sp,
          color = TdTextSecondaryDark
        )
      }
    }

    // Generating Overlay Dialog
    if (isGenerating) {
      Dialog(onDismissRequest = { /* Prevent dismiss while generating */ }) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          color = TdSurfaceElevatedDark,
          shape = RoundedCornerShape(24.dp),
          tonalElevation = 8.dp
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(
              color = TdYellowBright,
              strokeWidth = 4.dp,
              modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
              text = "TD Mahant Cartoon Studio",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = generationMessage.ifBlank { "Creating your cartoon animation..." },
              fontSize = 13.sp,
              color = TdYellowBright,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
              color = TdBluePrimary,
              trackColor = TdSurfaceVariantDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Character: $selectedCharacter • Style: $selectedStyle • ${selectedDuration}s",
              fontSize = 11.sp,
              color = TdTextSecondaryDark
            )
          }
        }
      }
    }
  }
}
