package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.model.CoinTransaction
import com.example.data.model.TransactionType
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
  viewModel: MainViewModel,
  onNavigateToReferral: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val transactions by viewModel.transactions.collectAsState()

  var showRechargeModal by remember { mutableStateOf(false) }

  val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .padding(horizontal = 16.dp)
      .testTag("wallet_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Coin Wallet",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Main Coin Balance Card
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
            Brush.horizontalGradient(
              listOf(
                TdPurpleDark,
                TdBlueDark
              )
            )
          )
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "AVAILABLE BALANCE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TdYellowGold,
              letterSpacing = 1.sp
            )

            Surface(
              color = TdYellowBright.copy(alpha = 0.2f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Active Creator",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TdYellowBright,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(TdYellowBright, TdYellowDark))),
              contentAlignment = Alignment.Center
            ) {
              Text("🪙", fontSize = 22.sp)
            }

            Text(
              text = "${currentUser?.coinBalance ?: 0}",
              fontSize = 36.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )

            Text(
              text = "Coins",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = TdYellowBright
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Rule Summary Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Video Creation", fontSize = 11.sp, color = TdTextSecondaryDark)
              Text("10 Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
              Text("Welcome Bonus", fontSize = 11.sp, color = TdTextSecondaryDark)
              Text("+200 Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TdSuccessGreen)
            }
            Column {
              Text("Referral Bonus", fontSize = 11.sp, color = TdTextSecondaryDark)
              Text("+50 Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TdYellowBright)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Action Buttons: Recharge & Earn Free Coins
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { showRechargeModal = true },
              modifier = Modifier
                .weight(1f)
                .testTag("btn_recharge_coins"),
              colors = ButtonDefaults.buttonColors(
                containerColor = TdYellowBright,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(imageVector = Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Get Coins", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = onNavigateToReferral,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, TdPurpleLight),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp), tint = TdYellowBright)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Earn Free", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Transaction History Header
    Text(
      text = "Transaction History",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (transactions.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Text("No coin transactions yet", color = TdTextSecondaryDark, fontSize = 13.sp)
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(transactions, key = { it.id }) { tx ->
          TransactionItem(tx = tx, dateStr = dateFormat.format(Date(tx.timestamp)))
        }
      }
    }
  }

  // Recharge Modal Dialog
  if (showRechargeModal) {
    RechargeModal(
      onDismiss = { showRechargeModal = false },
      onPurchase = { coins, amount ->
        viewModel.buyCoins(coins, amount)
        showRechargeModal = false
      }
    )
  }
}

@Composable
fun TransactionItem(tx: CoinTransaction, dateStr: String) {
  val isPositive = tx.amount > 0

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = TdSurfaceDark),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
              when (tx.type) {
                TransactionType.WELCOME_BONUS -> TdYellowAccent.copy(alpha = 0.2f)
                TransactionType.REFERRAL_BONUS -> TdPurpleSecondary.copy(alpha = 0.2f)
                TransactionType.VIDEO_CREATION -> TdBluePrimary.copy(alpha = 0.2f)
                TransactionType.RECHARGE -> TdSuccessGreen.copy(alpha = 0.2f)
                TransactionType.REFUND -> TdYellowDark.copy(alpha = 0.2f)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = when (tx.type) {
              TransactionType.WELCOME_BONUS -> "🎁"
              TransactionType.REFERRAL_BONUS -> "👥"
              TransactionType.VIDEO_CREATION -> "🎬"
              TransactionType.RECHARGE -> "💳"
              TransactionType.REFUND -> "↩️"
            },
            fontSize = 18.sp
          )
        }

        Column {
          Text(
            text = tx.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = tx.description,
            fontSize = 11.sp,
            color = TdTextSecondaryDark
          )
          Text(
            text = dateStr,
            fontSize = 10.sp,
            color = TdTextSecondaryDark.copy(alpha = 0.7f)
          )
        }
      }

      Text(
        text = if (isPositive) "+${tx.amount}" else "${tx.amount}",
        fontSize = 15.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (isPositive) TdSuccessGreen else TdErrorRed
      )
    }
  }
}

@Composable
fun RechargeModal(
  onDismiss: () -> Unit,
  onPurchase: (Int, Double) -> Unit
) {
  val packs = listOf(
    Triple(50, 29.0, "Starter Pack • 5 Videos"),
    Triple(200, 99.0, "Creator Pack • 20 Videos (Popular)"),
    Triple(500, 199.0, "Pro Studio Pack • 50 Videos (Best Value)")
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("🪙 Get More Coins", color = Color.White, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          "Secure UPI & Instant Activation. Each cartoon video costs only 10 coins.",
          fontSize = 12.sp,
          color = TdTextSecondaryDark
        )

        packs.forEach { (coins, inr, desc) ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { onPurchase(coins, inr) }
              .border(1.dp, TdYellowBright.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            color = TdSurfaceDark
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
                  text = "$coins Coins",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = Color.White
                )
                Text(
                  text = desc,
                  fontSize = 11.sp,
                  color = TdYellowBright
                )
              }

              Surface(
                color = TdYellowBright,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "₹$inr",
                  color = Color.Black,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = Color.White)
      }
    },
    containerColor = TdSurfaceElevatedDark
  )
}
