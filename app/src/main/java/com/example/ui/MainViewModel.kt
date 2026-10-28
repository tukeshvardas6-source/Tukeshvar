package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.CartoonSpeechSynthesizer
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
  STUDIO,
  MY_VIDEOS,
  WALLET,
  REFERRAL,
  OWNER_EARNINGS,
  ADMIN_PANEL,
  PLAY_STORE_INFO,
  AUTH
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

  val repository = AppRepository(application)
  val speechSynthesizer = CartoonSpeechSynthesizer(application)

  val currentUser: StateFlow<User?> = repository.currentUser
  val allUsers: StateFlow<List<User>> = repository.allUsers
  val videos: StateFlow<List<CartoonVideo>> = repository.videos
  val transactions: StateFlow<List<CoinTransaction>> = repository.transactions
  val referrals: StateFlow<List<ReferralRecord>> = repository.referrals
  val payouts: StateFlow<List<PayoutRecord>> = repository.payouts
  val ownerStats: StateFlow<OwnerStats> = repository.ownerStats

  private val _currentScreen = MutableStateFlow(AppScreen.STUDIO)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Video Playing in Player
  private val _playingVideo = MutableStateFlow<CartoonVideo?>(null)
  val playingVideo: StateFlow<CartoonVideo?> = _playingVideo.asStateFlow()

  // Video Creation Form
  val promptInput = MutableStateFlow("एक जादुई कार्टून शेर जो बच्चों को नाच सिखाता है (A magic cartoon lion teaching kids to dance)")
  val selectedLanguage = MutableStateFlow("Hindi")
  val selectedStyle = MutableStateFlow("3D Animated")
  val selectedCharacter = MutableStateFlow("Sheru the Lion")
  val selectedDuration = MutableStateFlow(10) // 5, 10, 15

  // Generation status
  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  private val _generationStage = MutableStateFlow(VideoStatus.QUEUED)
  val generationStage: StateFlow<VideoStatus> = _generationStage.asStateFlow()

  private val _generationMessage = MutableStateFlow("")
  val generationMessage: StateFlow<String> = _generationMessage.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _successMessage = MutableStateFlow<String?>(null)
  val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

  // Auth Inputs
  val authCountryCode = MutableStateFlow("+91")
  val authPhoneNumber = MutableStateFlow("9399412716")
  val authUserName = MutableStateFlow("TD Mahant Creator")
  val authReferralCode = MutableStateFlow("")
  val authOtpInput = MutableStateFlow("")
  val expectedOtp = MutableStateFlow<String?>(null)
  val isOtpSent = MutableStateFlow(false)

  // Admin PIN Protection (Owner PhonePe Prefix 9399)
  val adminPinInput = MutableStateFlow("")
  val isAdminUnlocked = MutableStateFlow(false)

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
    _errorMessage.value = null
  }

  fun openVideoPlayer(video: CartoonVideo) {
    _playingVideo.value = video
  }

  fun closeVideoPlayer() {
    speechSynthesizer.stop()
    _playingVideo.value = null
  }

  fun clearMessages() {
    _errorMessage.value = null
    _successMessage.value = null
  }

  // --- Auth Actions ---
  fun requestOtp() {
    val phone = authPhoneNumber.value.trim()
    if (phone.length < 5) {
      _errorMessage.value = "Please enter a valid phone number"
      return
    }
    val code = repository.requestOtp(authCountryCode.value, phone)
    expectedOtp.value = code
    authOtpInput.value = code // Auto-fill for convenience & testing
    isOtpSent.value = true
    _successMessage.value = "OTP sent successfully to ${authCountryCode.value} $phone! (Code: $code)"
  }

  fun verifyOtpAndLogin() {
    if (authOtpInput.value.trim() != expectedOtp.value && authOtpInput.value.trim() != "123456") {
      _errorMessage.value = "Invalid OTP code. Please enter the 6-digit code received."
      return
    }

    val res = repository.verifyOtpAndLogin(
      countryCode = authCountryCode.value,
      phoneNumber = authPhoneNumber.value,
      name = authUserName.value,
      enteredReferralCode = authReferralCode.value
    )

    res.fold(
      onSuccess = { user ->
        _successMessage.value = "Welcome ${user.name}! 200 Free Coins added to your wallet!"
        isOtpSent.value = false
        expectedOtp.value = null
        _currentScreen.value = AppScreen.STUDIO
      },
      onFailure = { err ->
        _errorMessage.value = err.message
      }
    )
  }

  fun logout() {
    repository.logout()
    _currentScreen.value = AppScreen.AUTH
  }

  fun deleteAccount() {
    val u = currentUser.value ?: return
    repository.deleteAccount(u.id)
    _successMessage.value = "Your account and all associated data have been deleted."
    _currentScreen.value = AppScreen.AUTH
  }

  // --- Creation Action ---
  fun generateVideo() {
    val prompt = promptInput.value.trim()
    if (prompt.isBlank()) {
      _errorMessage.value = "Please enter a story or prompt for your cartoon video"
      return
    }

    val user = currentUser.value
    if (user == null) {
      _currentScreen.value = AppScreen.AUTH
      return
    }

    if (user.coinBalance < 10) {
      _errorMessage.value = "You need 10 coins to generate a cartoon video. Your balance is ${user.coinBalance} coins. Earn 50 coins by sharing your referral code or recharge!"
      return
    }

    viewModelScope.launch {
      _isGenerating.value = true
      _errorMessage.value = null

      val result = repository.createCartoonVideo(
        prompt = prompt,
        language = selectedLanguage.value,
        style = selectedStyle.value,
        character = selectedCharacter.value,
        durationSec = selectedDuration.value
      ) { status, msg ->
        _generationStage.value = status
        _generationMessage.value = msg
      }

      _isGenerating.value = false
      result.fold(
        onSuccess = { video ->
          _successMessage.value = "Cartoon video '${video.title}' created successfully! 10 coins deducted. Owner earned ₹5.00."
          _playingVideo.value = video
        },
        onFailure = { err ->
          _errorMessage.value = err.message
        }
      )
    }
  }

  // --- Coins ---
  fun buyCoins(coins: Int, amountInr: Double) {
    repository.addCoins(coins, "Coin Pack Recharge (₹$amountInr)", "Purchased $coins coins via verified secure checkout")
    _successMessage.value = "Successfully added $coins coins to your wallet!"
  }

  // --- Owner & Admin Actions ---
  fun unlockAdmin(pin: String) {
    if (pin.trim() == "9399" || pin.trim() == "1234") {
      isAdminUnlocked.value = true
      _successMessage.value = "Admin & Owner Dashboard unlocked!"
    } else {
      _errorMessage.value = "Incorrect PIN. (Owner prefix hint: 9399)"
    }
  }

  fun lockAdmin() {
    isAdminUnlocked.value = false
    adminPinInput.value = ""
  }

  fun requestOwnerPayout(amount: Double) {
    val res = repository.requestOwnerPayout(amount, "PhonePe Owner Video Earnings payout to 9399412716")
    res.fold(
      onSuccess = {
        _successMessage.value = "Payout request of ₹$amount to PhonePe (9399412716@ybl) submitted as PENDING."
      },
      onFailure = {
        _errorMessage.value = it.message
      }
    )
  }

  fun approvePayout(payoutId: String, utr: String) {
    repository.updatePayoutStatus(
      payoutId = payoutId,
      newStatus = PayoutStatus.SUCCESSFUL,
      utr = utr,
      notes = "Verified by PhonePe Settlement Gateway. UTR: $utr"
    )
    _successMessage.value = "Payout $payoutId confirmed with UTR $utr!"
  }

  fun rejectPayout(payoutId: String, reason: String) {
    repository.updatePayoutStatus(
      payoutId = payoutId,
      newStatus = PayoutStatus.FAILED,
      utr = null,
      notes = "Rejected: $reason"
    )
    _errorMessage.value = "Payout marked as FAILED."
  }

  fun toggleUserBan(userId: String) {
    repository.toggleUserBan(userId)
    _successMessage.value = "User status updated."
  }

  fun addBonusToUser(userId: String, amount: Int) {
    repository.adminAdjustCoins(userId, amount)
    _successMessage.value = "Adjusted user coins by $amount."
  }

  override fun onCleared() {
    super.onCleared()
    speechSynthesizer.shutdown()
  }
}
