package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PayoutRecord
import com.example.data.model.PayoutStatus
import com.example.data.model.User
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
  viewModel: MainViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()
  val adminPin by viewModel.adminPinInput.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()
  val videos by viewModel.videos.collectAsState()
  val payouts by viewModel.payouts.collectAsState()
  val ownerStats by viewModel.ownerStats.collectAsState()

  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Overview", "Payouts", "Users", "Videos")

  var approvingPayout by remember { mutableStateOf<PayoutRecord?>(null) }
  var utrInput by remember { mutableStateOf("UPI/9399412716/2026") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .padding(horizontal = 16.dp)
      .testTag("admin_dashboard_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
          text = "Creator & Admin Portal",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      if (isAdminUnlocked) {
        TextButton(onClick = { viewModel.lockAdmin() }) {
          Text("Lock Admin", color = TdYellowBright, fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (!isAdminUnlocked) {
      // PIN Lock View
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier.fillMaxWidth(0.9f),
          colors = CardDefaults.cardColors(containerColor = TdSurfaceElevatedDark),
          shape = RoundedCornerShape(20.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(TdPurpleSecondary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Owner Admin Access",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "Enter the 4-digit PIN to access user moderation and PhonePe payout approvals.",
              fontSize = 12.sp,
              color = TdTextSecondaryDark,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
              value = adminPin,
              onValueChange = { viewModel.adminPinInput.value = it },
              placeholder = { Text("Enter PIN (e.g. 9399)", color = TdTextSecondaryDark) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = TdYellowBright,
                unfocusedBorderColor = TdSurfaceVariantDark
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_pin_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = { viewModel.unlockAdmin(adminPin) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_unlock_admin"),
              colors = ButtonDefaults.buttonColors(containerColor = TdYellowBright, contentColor = Color.Black),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Unlock Dashboard", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      // Unlocked Admin Panel Tabs
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
            text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      when (selectedTab) {
        0 -> {
          // Overview
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AdminStatCard("Registered Users", "${allUsers.size}", TdBlueLight, Modifier.weight(1f))
                AdminStatCard("Total Videos", "${videos.size}", TdPurpleLight, Modifier.weight(1f))
              }
            }

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AdminStatCard("Coins Circulated", "${allUsers.sumOf { it.coinBalance }}", TdYellowBright, Modifier.weight(1f))
                AdminStatCard("Accrued Payouts", "₹${"%.2f".format(ownerStats.totalAccruedEarningsInr)}", TdSuccessGreen, Modifier.weight(1f))
              }
            }

            item {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TdSurfaceDark,
                shape = RoundedCornerShape(14.dp)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text("System Configuration", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                  Spacer(modifier = Modifier.height(8.dp))
                  Text("• App Name: TD Mahant Cartoon Videos", color = TdTextSecondaryDark, fontSize = 12.sp)
                  Text("• Welcome Bonus: 200 Free Coins (One-time)", color = TdTextSecondaryDark, fontSize = 12.sp)
                  Text("• Referral Bonus: 50 Coins per verified user", color = TdTextSecondaryDark, fontSize = 12.sp)
                  Text("• Video Creation Cost: 10 Coins per video", color = TdTextSecondaryDark, fontSize = 12.sp)
                  Text("• Owner PhonePe: 9399412716 (${ownerStats.verifiedUpiId})", color = TdYellowBright, fontSize = 12.sp)
                  Text("• Owner Video Earnings Rate: ₹5.00 / video", color = TdYellowBright, fontSize = 12.sp)
                }
              }
            }
          }
        }

        1 -> {
          // Payouts Approval Tab
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
          ) {
            items(payouts, key = { it.id }) { p ->
              Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TdSurfaceDark,
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "₹${"%.2f".format(p.amountInr)}",
                      fontSize = 18.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )

                    Surface(
                      color = when (p.status) {
                        PayoutStatus.SUCCESSFUL -> TdSuccessGreen.copy(alpha = 0.2f)
                        PayoutStatus.PROCESSING -> TdYellowBright.copy(alpha = 0.2f)
                        PayoutStatus.FAILED -> TdErrorRed.copy(alpha = 0.2f)
                        else -> TdBlueLight.copy(alpha = 0.2f)
                      },
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = p.status.name,
                        color = when (p.status) {
                          PayoutStatus.SUCCESSFUL -> TdSuccessGreen
                          PayoutStatus.PROCESSING -> TdYellowBright
                          PayoutStatus.FAILED -> TdErrorRed
                          else -> TdBlueLight
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(4.dp))
                  Text("UPI: ${p.destinationUpi} (${p.phonePeNumber})", color = TdYellowGold, fontSize = 12.sp)
                  Text(p.notes, color = TdTextSecondaryDark, fontSize = 11.sp)

                  if (p.status != PayoutStatus.SUCCESSFUL) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      Button(
                        onClick = {
                          approvingPayout = p
                          utrInput = "UPI/9399412716/${System.currentTimeMillis().toString().takeLast(6)}"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TdSuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                      ) {
                        Text("Approve & Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }

                      OutlinedButton(
                        onClick = {
                          viewModel.rejectPayout(p.id, "Rejected by administrator review")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TdErrorRed),
                        shape = RoundedCornerShape(8.dp)
                      ) {
                        Text("Reject", fontSize = 11.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Users Tab
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
          ) {
            items(allUsers, key = { it.id }) { u ->
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
                    Text(u.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text("${u.countryCode} ${u.phoneNumber} • Code: ${u.referralCode}", fontSize = 11.sp, color = TdTextSecondaryDark)
                    Text("Coins: ${u.coinBalance} 🪙", fontSize = 12.sp, color = TdYellowBright, fontWeight = FontWeight.Bold)
                    if (u.isBanned) {
                      Text("⚠️ BANNED", fontSize = 10.sp, color = TdErrorRed, fontWeight = FontWeight.Bold)
                    }
                  }

                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { viewModel.addBonusToUser(u.id, 50) }) {
                      Text("+50", color = TdSuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    IconButton(onClick = { viewModel.toggleUserBan(u.id) }) {
                      Icon(
                        imageVector = if (u.isBanned) Icons.Default.LockOpen else Icons.Default.Block,
                        contentDescription = "Toggle Ban",
                        tint = if (u.isBanned) TdSuccessGreen else TdErrorRed
                      )
                    }
                  }
                }
              }
            }
          }
        }

        3 -> {
          // Video Moderation
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
          ) {
            items(videos, key = { it.id }) { vid ->
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
                  Column(modifier = Modifier.weight(1f)) {
                    Text(vid.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text("\"${vid.prompt}\"", fontSize = 11.sp, color = TdTextSecondaryDark, maxLines = 1)
                    Text("${vid.character} • ${vid.style} • ${vid.durationSec}s", fontSize = 10.sp, color = TdYellowBright)
                  }

                  IconButton(onClick = { viewModel.repository.deleteVideo(vid.id) }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TdErrorRed)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Approve Payout Dialog
  approvingPayout?.let { p ->
    AlertDialog(
      onDismissRequest = { approvingPayout = null },
      title = { Text("Confirm PhonePe Payout", color = Color.White) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Confirm settlement of ₹${p.amountInr} to PhonePe UPI: ${p.destinationUpi}.", fontSize = 12.sp, color = TdTextSecondaryDark)
          OutlinedTextField(
            value = utrInput,
            onValueChange = { utrInput = it },
            label = { Text("Bank UTR / Transaction Reference", color = TdTextSecondaryDark) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = TdYellowBright,
              unfocusedBorderColor = TdSurfaceVariantDark
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.approvePayout(p.id, utrInput)
            approvingPayout = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = TdSuccessGreen)
        ) {
          Text("Confirm Settlement", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { approvingPayout = null }) {
          Text("Cancel", color = Color.White)
        }
      },
      containerColor = TdSurfaceElevatedDark
    )
  }
}

@Composable
fun AdminStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    color = TdSurfaceDark,
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(label, fontSize = 11.sp, color = TdTextSecondaryDark)
      Spacer(modifier = Modifier.height(4.dp))
      Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
    }
  }
}
