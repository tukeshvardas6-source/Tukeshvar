package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PayoutRecord
import com.example.data.model.PayoutStatus
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OwnerEarningsScreen(
  viewModel: MainViewModel,
  onNavigateToAdmin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ownerStats by viewModel.ownerStats.collectAsState()
  val payouts by viewModel.payouts.collectAsState()
  val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()

  var showPayoutDialog by remember { mutableStateOf(false) }
  var payoutAmountInput by remember { mutableStateOf("100") }
  val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .padding(horizontal = 16.dp)
      .testTag("owner_earnings_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Header & Admin Switch
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Owner Earnings Dashboard",
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "PhonePe: ${ownerStats.phonePeNumber} • ₹5/video",
          fontSize = 11.sp,
          color = TdYellowBright
        )
      }

      Button(
        onClick = onNavigateToAdmin,
        colors = ButtonDefaults.buttonColors(
          containerColor = TdPurpleSecondary,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("btn_goto_admin")
      ) {
        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(if (isAdminUnlocked) "Admin Panel" else "Admin Lock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Verified Destination Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = TdSurfaceElevatedDark,
      shape = RoundedCornerShape(14.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, TdYellowBright.copy(alpha = 0.5f))
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF5f259f)), // PhonePe Purple
              contentAlignment = Alignment.Center
            ) {
              Text("पे", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Column {
              Text(
                text = "PhonePe Verified Destination",
                fontSize = 11.sp,
                color = TdTextSecondaryDark
              )
              Text(
                text = ownerStats.verifiedUpiId,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Surface(
            color = TdSuccessGreen.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = TdSuccessGreen, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Verified", color = TdSuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Registered Owner: TD Mahant • Mobile: ${ownerStats.phonePeNumber}",
          fontSize = 11.sp,
          color = TdYellowBright
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Earnings Overview Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Total Accrued
      Surface(
        modifier = Modifier.weight(1f),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Accrued Earnings", fontSize = 11.sp, color = TdTextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "₹${"%.2f".format(ownerStats.totalAccruedEarningsInr)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TdYellowBright
          )
          Text(
            text = "${ownerStats.totalVideosGenerated} videos @ ₹5 each",
            fontSize = 10.sp,
            color = TdTextSecondaryDark
          )
        }
      }

      // Pending Payouts
      Surface(
        modifier = Modifier.weight(1f),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Pending Settlements", fontSize = 11.sp, color = TdTextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "₹${"%.2f".format(ownerStats.pendingEarningsInr)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFBBF24)
          )
          Text(
            text = "Awaiting verification",
            fontSize = 10.sp,
            color = TdTextSecondaryDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Completed Payouts
      Surface(
        modifier = Modifier.weight(1f),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Completed Payments", fontSize = 11.sp, color = TdTextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "₹${"%.2f".format(ownerStats.paidEarningsInr)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TdSuccessGreen
          )
          Text(
            text = "UTR confirmed",
            fontSize = 10.sp,
            color = TdTextSecondaryDark
          )
        }
      }

      // Initial ₹100 Status
      val initialReq = payouts.find { it.id == "payout_init_100" }
      Surface(
        modifier = Modifier.weight(1f),
        color = TdSurfaceDark,
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Initial ₹100 Request", fontSize = 11.sp, color = TdTextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = initialReq?.status?.name ?: "PENDING",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = when (initialReq?.status) {
              PayoutStatus.SUCCESSFUL -> TdSuccessGreen
              PayoutStatus.PROCESSING -> TdYellowBright
              PayoutStatus.FAILED -> TdErrorRed
              else -> Color.White
            }
          )
          Text(
            text = "PhonePe: 9399412716",
            fontSize = 10.sp,
            color = TdTextSecondaryDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Request Settlement Button
    Button(
      onClick = { showPayoutDialog = true },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("btn_request_payout"),
      colors = ButtonDefaults.buttonColors(
        containerColor = TdYellowBright,
        contentColor = Color.Black
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("Request PhonePe Payout Settlement", fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Payout Ledger & Audit Records",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(bottom = 90.dp),
      modifier = Modifier.weight(1f)
    ) {
      items(payouts, key = { it.id }) { payout ->
        PayoutItemCard(payout = payout, dateStr = dateFormat.format(Date(payout.requestedAt)))
      }
    }
  }

  // Request Payout Dialog
  if (showPayoutDialog) {
    AlertDialog(
      onDismissRequest = { showPayoutDialog = false },
      title = { Text("Request PhonePe Payout", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Earnings are credited to PhonePe: ${ownerStats.phonePeNumber} (${ownerStats.verifiedUpiId}). " +
                "Status will be marked as PENDING until verified by payment gateway.",
            fontSize = 12.sp,
            color = TdTextSecondaryDark
          )

          OutlinedTextField(
            value = payoutAmountInput,
            onValueChange = { payoutAmountInput = it },
            label = { Text("Amount (₹)", color = TdTextSecondaryDark) },
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
            val amt = payoutAmountInput.toDoubleOrNull() ?: 0.0
            viewModel.requestOwnerPayout(amt)
            showPayoutDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = TdYellowBright, contentColor = Color.Black)
        ) {
          Text("Submit Request", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showPayoutDialog = false }) {
          Text("Cancel", color = Color.White)
        }
      },
      containerColor = TdSurfaceElevatedDark
    )
  }
}

@Composable
fun PayoutItemCard(payout: PayoutRecord, dateStr: String) {
  val (statusColor, statusIcon) = when (payout.status) {
    PayoutStatus.SUCCESSFUL -> TdSuccessGreen to Icons.Default.CheckCircle
    PayoutStatus.PROCESSING -> TdYellowBright to Icons.Default.HourglassTop
    PayoutStatus.PENDING -> TdBlueLight to Icons.Default.Schedule
    PayoutStatus.FAILED -> TdErrorRed to Icons.Default.Cancel
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "₹${"%.2f".format(payout.amountInr)}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Color.White
          )
          Text(
            text = "Destination: ${payout.destinationUpi}",
            fontSize = 11.sp,
            color = TdTextSecondaryDark
          )
        }

        Surface(
          color = statusColor.copy(alpha = 0.18f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = payout.status.name,
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = payout.notes,
        fontSize = 11.sp,
        color = TdYellowGold
      )

      if (!payout.utrReference.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Bank UTR: ${payout.utrReference}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TdSuccessGreen
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Requested: $dateStr",
        fontSize = 10.sp,
        color = TdTextSecondaryDark.copy(alpha = 0.7f)
      )
    }
  }
}
