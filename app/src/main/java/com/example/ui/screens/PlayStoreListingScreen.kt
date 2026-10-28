package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PlayStoreListingScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Store Listing", "Privacy Policy", "Terms of Service")
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .padding(horizontal = 16.dp)
      .testTag("play_store_listing_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(TdSurfaceDark)
      ) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
      }

      Text(
        text = "Google Play Store & Policy",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = TdSurfaceDark,
      contentColor = TdYellowBright,
      modifier = Modifier.clip(RoundedCornerShape(12.dp))
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(bottom = 90.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // Store Listing Info
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Official Store Metadata",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TdYellowBright
              )
              Spacer(modifier = Modifier.height(10.dp))

              Text("App Title (Exact Name)", fontSize = 11.sp, color = TdTextSecondaryDark)
              Text("TD Mahant Cartoon Videos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
              Text("24 characters (Complies with Google Play 30-character policy)", fontSize = 10.sp, color = TdSuccessGreen)

              Spacer(modifier = Modifier.height(12.dp))

              Text("Short Description", fontSize = 11.sp, color = TdTextSecondaryDark)
              Text("Create AI cartoon videos in Hindi & English, earn coins, and share stories.", fontSize = 13.sp, color = Color.White)

              Spacer(modifier = Modifier.height(12.dp))

              Text("Full Description", fontSize = 11.sp, color = TdTextSecondaryDark)
              Text(
                """
Welcome to TD Mahant Cartoon Videos – the ultimate AI-powered animation studio designed for creators, storytellers, and cartoon lovers worldwide!

Bring your imagination to life with our high-tech cartoon video generator:
✨ AI Cartoon Generation: Enter any prompt in Hindi, English, or your favorite language and create animated cartoon scenes.
🎨 Multiple Styles: Choose from 3D Animated, Classic 2D Cartoon, Anime Toon, Claymation, and Comic Book visuals.
🦁 Iconic Characters: Direct Sheru the Lion, Raju & Chintu, Superhero Kids, Princess Aanya, Detective Bhalu, and more!
🪙 200 Free Welcome Coins: Start creating immediately with 200 bonus coins upon mobile registration.
🎁 Referral Rewards: Earn 50 bonus coins for every friend who joins with your unique referral code.
💰 Transparent Creator Earnings: Track owner earnings with PhonePe settlement integration.
📱 Export & Share: Download high-quality videos and share directly with friends and family on social media.

Publishing Compliance:
• Application ID: com.aistudio.tdmahant.cartoons
• Content Rating: Everyone 10+ (Guidance for Mild Fantasy Violence)
• Developer Contact: tukeshvardas6@gmail.com
                """.trimIndent(),
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 18.sp
              )
            }
          }
        }

        1 -> {
          // Privacy Policy
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Privacy Policy for TD Mahant Cartoon Videos",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TdYellowBright
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text("Effective Date: October 2026", fontSize = 11.sp, color = TdTextSecondaryDark)

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                """
1. Information We Collect:
We collect your mobile phone number and optional display name solely for authentication and OTP verification purposes. We do not sell, rent, or trade personal data to third parties.

2. Zero-Permission Storage Policy:
Our application uses standard Android scoped media selectors and local app storage to download and display your generated cartoon videos without requesting sensitive broad storage permissions.

3. Video Generation & Content:
User prompts are processed by secure AI APIs (including Google Gemini) solely to produce cartoon story scripts and animation directions. Prompts are never used to train public models without consent.

4. Coin Wallet & Earnings:
Transactions in the coin wallet and owner payouts (linked to PhonePe 9399412716@ybl) are logged for accounting accuracy. Real payments require explicit confirmation before being marked as successful.

5. Right to Erasure & Account Deletion:
In accordance with Google Play User Data policies, users can permanently delete their account and all associated video data directly inside the app's Profile screen with one tap.

6. Contact & Data Protection Officer:
For any privacy inquiries or deletion requests:
Email: tukeshvardas6@gmail.com
Owner: TD Mahant
                """.trimIndent(),
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 18.sp
              )
            }
          }
        }

        2 -> {
          // Terms of Service
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Terms of Service",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TdYellowBright
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                """
1. Acceptance of Terms:
By registering for and using TD Mahant Cartoon Videos, you agree to these Terms of Service.

2. Responsible Creation:
You agree not to submit prompts that are defamatory, sexually explicit, promote hate speech, or infringe upon third-party copyrights. Inappropriate content will be removed by moderators.

3. Virtual Coins & Refunds:
Each cartoon video creation costs 10 coins. Coins are strictly virtual within the app. If video generation fails due to a network or server error, zero coins are deducted.

4. Referral Fairness:
Referral bonuses (+50 coins) require successful OTP verification from genuine, distinct mobile devices. Automated or fraudulent registrations will lead to account suspension.

5. Modifications:
TD Mahant reserves the right to modify features, coin prices, and promotional bonuses at any time.
                """.trimIndent(),
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 18.sp
              )
            }
          }
        }
      }
    }
  }
}
