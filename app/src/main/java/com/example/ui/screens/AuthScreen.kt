package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  viewModel: MainViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val countryCode by viewModel.authCountryCode.collectAsState()
  val phoneNumber by viewModel.authPhoneNumber.collectAsState()
  val userName by viewModel.authUserName.collectAsState()
  val referralCode by viewModel.authReferralCode.collectAsState()
  val otpInput by viewModel.authOtpInput.collectAsState()
  val expectedOtp by viewModel.expectedOtp.collectAsState()
  val isOtpSent by viewModel.isOtpSent.collectAsState()

  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  var showCountryPicker by remember { mutableStateOf(false) }

  val internationalCodes = listOf(
    "+91" to "🇮🇳 India",
    "+1" to "🇺🇸 United States / Canada",
    "+44" to "🇬🇧 United Kingdom",
    "+971" to "🇦🇪 United Arab Emirates",
    "+61" to "🇦🇺 Australia",
    "+49" to "🇩🇪 Germany",
    "+33" to "🇫🇷 France",
    "+81" to "🇯🇵 Japan",
    "+65" to "🇸🇬 Singapore",
    "+966" to "🇸🇦 Saudi Arabia"
  )

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(TdBackgroundDark)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp)
      .testTag("auth_screen"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
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
        text = if (currentUser != null) "My Account" else "Mobile Login",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Box(modifier = Modifier.size(36.dp))
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Logo & Header
    Image(
      painter = painterResource(id = R.drawable.ic_td_mahant_logo),
      contentDescription = "TD Mahant Logo",
      modifier = Modifier
        .size(76.dp)
        .clip(RoundedCornerShape(16.dp)),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "TD Mahant Cartoon Videos",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Text(
      text = "AI Cartoon Studio & Creator Rewards",
      fontSize = 12.sp,
      color = TdYellowBright
    )

    Spacer(modifier = Modifier.height(20.dp))

    if (currentUser != null) {
      // Logged in User Profile View
      val user = currentUser!!
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TdSurfaceElevatedDark),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(TdBluePrimary, TdPurpleSecondary))),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.name.take(1).uppercase(),
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = user.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = "${user.countryCode} ${user.phoneNumber}",
            fontSize = 13.sp,
            color = TdTextSecondaryDark
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Coin & Referral Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              modifier = Modifier.weight(1f),
              color = TdSurfaceDark,
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("Coin Balance", fontSize = 11.sp, color = TdTextSecondaryDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${user.coinBalance} 🪙", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TdYellowBright)
              }
            }

            Surface(
              modifier = Modifier.weight(1f),
              color = TdSurfaceDark,
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("Referral Code", fontSize = 11.sp, color = TdTextSecondaryDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(user.referralCode, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Welcome bonus status
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TdSuccessGreen.copy(alpha = 0.15f),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TdSuccessGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("200 Welcome Bonus Coins Credited", color = TdSuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Logout Button
          Button(
            onClick = { viewModel.logout() },
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("btn_logout"),
            colors = ButtonDefaults.buttonColors(containerColor = TdSurfaceVariantDark, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Logout", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Delete Account Button (Required by Google Play Policy)
          OutlinedButton(
            onClick = { showDeleteConfirmDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("btn_delete_account"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TdErrorRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, TdErrorRed.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp), tint = TdErrorRed)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Delete Account & Data", fontSize = 12.sp)
          }
        }
      }
    } else {
      // Login & Registration Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TdSurfaceElevatedDark),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Welcome Promo Banner
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TdYellowBright.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TdYellowBright.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("🎁", fontSize = 22.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Get 200 Free Coins",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = TdYellowBright
                )
                Text(
                  text = "Awarded once per new mobile registration. Create 20 cartoon videos free!",
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // User Name
          Text("Your Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = userName,
            onValueChange = { viewModel.authUserName.value = it },
            placeholder = { Text("e.g. Rahul Sharma", color = TdTextSecondaryDark) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = TdPurpleSecondary,
              unfocusedBorderColor = TdSurfaceVariantDark
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_user_name")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Mobile Number with International Country Code
          Text("Mobile Number (International)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Country Code Selector
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { showCountryPicker = true },
              color = TdSurfaceVariantDark,
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = countryCode, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
              }
            }

            // Phone Number Input
            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { viewModel.authPhoneNumber.value = it },
              placeholder = { Text("Enter 10-digit number", color = TdTextSecondaryDark) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = TdYellowBright,
                unfocusedBorderColor = TdSurfaceVariantDark
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("input_phone_number")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Referral Code (Optional)
          Text("Referral Code (Optional - +50 Coins to Inviter)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = referralCode,
            onValueChange = { viewModel.authReferralCode.value = it },
            placeholder = { Text("e.g. TDM-9399", color = TdTextSecondaryDark) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = TdYellowBright,
              unfocusedBorderColor = TdSurfaceVariantDark
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_referral_code")
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Request OTP Button
          Button(
            onClick = { viewModel.requestOtp() },
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("btn_request_otp"),
            colors = ButtonDefaults.buttonColors(containerColor = TdBluePrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isOtpSent) "Resend OTP Code" else "Send OTP Verification Code", fontWeight = FontWeight.Bold)
          }

          // OTP Input (Visible after requesting OTP)
          AnimatedVisibility(visible = isOtpSent) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
              Text(
                text = "Enter 6-Digit OTP Code",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TdYellowBright
              )
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = otpInput,
                onValueChange = { viewModel.authOtpInput.value = it },
                placeholder = { Text("6-digit code", color = TdTextSecondaryDark) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = TdSuccessGreen,
                  unfocusedBorderColor = TdSurfaceVariantDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_otp")
              )

              if (expectedOtp != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Demo Verification Code: $expectedOtp",
                  fontSize = 11.sp,
                  color = TdSuccessGreen
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { viewModel.verifyOtpAndLogin() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("btn_verify_otp"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = TdYellowBright,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Verify & Claim 200 Free Coins", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(30.dp))
  }

  // Country Picker Modal
  if (showCountryPicker) {
    AlertDialog(
      onDismissRequest = { showCountryPicker = false },
      title = { Text("Select Country Code", color = Color.White) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          internationalCodes.forEach { (code, name) ->
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  viewModel.authCountryCode.value = code
                  showCountryPicker = false
                },
              color = if (countryCode == code) TdPurpleDark else TdSurfaceDark
            ) {
              Text(
                text = "$name ($code)",
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(12.dp)
              )
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showCountryPicker = false }) {
          Text("Cancel", color = Color.White)
        }
      },
      containerColor = TdSurfaceElevatedDark
    )
  }

  // Delete Account Confirmation Dialog
  if (showDeleteConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmDialog = false },
      title = { Text("Delete Account Permanently?", color = Color.White) },
      text = {
        Text(
          "All your generated videos, coin balance, and referral data will be permanently wiped. This action cannot be undone.",
          color = TdTextSecondaryDark,
          fontSize = 13.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteAccount()
            showDeleteConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = TdErrorRed)
        ) {
          Text("Permanently Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmDialog = false }) {
          Text("Cancel", color = Color.White)
        }
      },
      containerColor = TdSurfaceElevatedDark
    )
  }
}
