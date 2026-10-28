package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReferralScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentUser by viewModel.currentUser.collectAsState()
  val referrals by viewModel.referrals.collectAsState()

  var copySuccess by remember { mutableStateOf(false) }

  val myCode = currentUser?.referralCode ?: "TDM-9399"
  val shareableLink = "https://tdmahant.app/ref/$myCode"

  val totalReferredCoins = referrals.sumOf { it.bonusCoins }
  val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .padding(horizontal = 16.dp)
      .testTag("referral_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Referral & Rewards",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Hero Referral Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp)),
      colors = CardDefaults.cardColors(containerColor = TdSurfaceElevatedDark),
      shape = RoundedCornerShape(20.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                TdPurpleSecondary,
                TdPurpleDark,
                TdBackgroundDark
              )
            )
          )
          .padding(20.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(TdYellowBright),
            contentAlignment = Alignment.Center
          ) {
            Text("🎁", fontSize = 28.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Earn 50 Free Coins Per Friend",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )

          Text(
            text = "Your friend gets 200 welcome coins, and you get 50 coins instantly when they verify their phone number!",
            fontSize = 12.sp,
            color = TdYellowBright,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Code Display Box
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.5.dp, TdYellowBright, RoundedCornerShape(14.dp)),
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(14.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "YOUR REFERRAL CODE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TdTextSecondaryDark
                )
                Text(
                  text = myCode,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TdYellowBright,
                  letterSpacing = 2.sp
                )
              }

              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("TD Mahant Referral Code", myCode)
                  clipboard.setPrimaryClip(clip)
                  copySuccess = true
                },
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(TdSurfaceVariantDark)
                  .testTag("btn_copy_referral")
              ) {
                Icon(
                  imageVector = if (copySuccess) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = "Copy Code",
                  tint = if (copySuccess) TdSuccessGreen else Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Share Button
          Button(
            onClick = {
              val shareMessage = """
                🎉 Join TD Mahant Cartoon Videos!
                Make incredible AI cartoon videos in Hindi & English!
                Use my referral code: $myCode to get 200 FREE COINS instantly!
                Download here: $shareableLink
              """.trimIndent()

              val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(intent, "Share TD Mahant Referral Code"))
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("btn_share_referral"),
            colors = ButtonDefaults.buttonColors(
              containerColor = TdYellowBright,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Referral Link & Code", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Stats Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Surface(
        modifier = Modifier.weight(1f),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Friends Joined", fontSize = 11.sp, color = TdTextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${referrals.size}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Surface(
        modifier = Modifier.weight(1f),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Coins Earned", fontSize = 11.sp, color = TdTextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "+$totalReferredCoins 🪙",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TdYellowBright
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Anti-fraud disclaimer notice
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = TdSurfaceDark.copy(alpha = 0.6f),
      shape = RoundedCornerShape(10.dp)
    ) {
      Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = TdBlueLight,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "Anti-Fraud Security: Only verified mobile registrations are rewarded. Self-referrals and duplicate accounts are strictly prevented.",
          fontSize = 11.sp,
          color = TdTextSecondaryDark
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Referral History",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (referrals.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "No referrals yet. Share your code to earn 50 coins!",
          color = TdTextSecondaryDark,
          fontSize = 12.sp
        )
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(referrals, key = { it.id }) { ref ->
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TdSurfaceDark,
            shape = RoundedCornerShape(12.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "New User: ${ref.referredPhone.take(6)}••••",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color.White
                )
                Text(
                  text = dateFormat.format(Date(ref.timestamp)),
                  fontSize = 10.sp,
                  color = TdTextSecondaryDark
                )
              }

              Surface(
                color = TdYellowAccent.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "+${ref.bonusCoins} Coins",
                  color = TdYellowBright,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
