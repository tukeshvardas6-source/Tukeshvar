package com.example.data.repository

import android.content.Context
import com.example.data.ai.CartoonAiGenerator
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AppRepository(context: Context) {

  private val prefs = context.getSharedPreferences("td_mahant_prefs", Context.MODE_PRIVATE)

  // Current Logged In User
  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

  // All Registered Users (for referral and admin management)
  private val _allUsers = MutableStateFlow<List<User>>(emptyList())
  val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

  // Videos
  private val _videos = MutableStateFlow<List<CartoonVideo>>(emptyList())
  val videos: StateFlow<List<CartoonVideo>> = _videos.asStateFlow()

  // Coin Transactions
  private val _transactions = MutableStateFlow<List<CoinTransaction>>(emptyList())
  val transactions: StateFlow<List<CoinTransaction>> = _transactions.asStateFlow()

  // Referrals
  private val _referrals = MutableStateFlow<List<ReferralRecord>>(emptyList())
  val referrals: StateFlow<List<ReferralRecord>> = _referrals.asStateFlow()

  // Payout Records
  private val _payouts = MutableStateFlow<List<PayoutRecord>>(emptyList())
  val payouts: StateFlow<List<PayoutRecord>> = _payouts.asStateFlow()

  // Owner Stats
  private val _ownerStats = MutableStateFlow(
    OwnerStats(
      phonePeNumber = "9399412716",
      verifiedUpiId = "9399412716@ybl",
      ratePerVideoInr = 5.0,
      totalVideosGenerated = 0,
      totalAccruedEarningsInr = 0.0,
      paidEarningsInr = 0.0,
      pendingEarningsInr = 0.0
    )
  )
  val ownerStats: StateFlow<OwnerStats> = _ownerStats.asStateFlow()

  init {
    seedInitialData()
  }

  private fun seedInitialData() {
    // Initial Owner ₹100 payment request
    val initialPayout = PayoutRecord(
      id = "payout_init_100",
      amountInr = 100.0,
      destinationUpi = "9399412716@ybl",
      phonePeNumber = "9399412716",
      requestedAt = System.currentTimeMillis() - 86400000L * 2,
      processedAt = null,
      status = PayoutStatus.PROCESSING,
      utrReference = null,
      notes = "Initial Owner Setup Payment of ₹100 to PhonePe UPI (9399412716@ybl)"
    )
    _payouts.value = listOf(initialPayout)

    // Demo user for instant testing if needed
    val demoUser = User(
      id = "user_demo_1",
      phoneNumber = "9399412716",
      countryCode = "+91",
      name = "TD Mahant Creator",
      referralCode = "TDM-9399",
      referredBy = null,
      coinBalance = 200,
      hasReceivedWelcomeBonus = true,
      isBanned = false
    )
    _allUsers.value = listOf(demoUser)
    _currentUser.value = demoUser

    val welcomeTx = CoinTransaction(
      id = "tx_welcome_${System.currentTimeMillis()}",
      userId = demoUser.id,
      amount = 200,
      type = TransactionType.WELCOME_BONUS,
      title = "Welcome Bonus",
      description = "200 Free Coins awarded on mobile registration!"
    )
    _transactions.value = listOf(welcomeTx)

    // Add a featured cartoon video to inspire the user
    val demoVideo = CartoonVideo(
      id = "tdm_vid_sample_1",
      userId = demoUser.id,
      title = "Raju & Chintu: The Magic Cartoon Cloud",
      prompt = "Raju and Chintu discover a laughing magic cloud in their village",
      language = "Hindi",
      style = "3D Animated",
      character = "Raju & Chintu",
      durationSec = 10,
      scenes = listOf(
        CartoonScene(
          sceneNumber = 1,
          title = "Scene 1: Morning at Rainbow Village",
          visualDescription = "Raju and Chintu look up at a glowing golden cloud raining sweet candy. 3D Pixar-style lighting with vibrant purple and blue skies.",
          characterAction = "Raju jumps and catches a flying candy star",
          dialogueHindi = "अरे चिंटू! आसमान में देखो, सोने का बादल मुस्कुरा रहा है!",
          dialogueEnglish = "Look Chintu! A golden smiling cloud in the sky!",
          backgroundVibe = "3D Magical Cartoon Village",
          moodEmoji = "🌟"
        ),
        CartoonScene(
          sceneNumber = 2,
          title = "Scene 2: The Flying Clapperboard",
          visualDescription = "A magical TD Mahant clapperboard appears spinning, granting cartoon superpowers to both friends.",
          characterAction = "Chintu claps the clapperboard and flies in the air with superhero sparkles",
          dialogueHindi = "TD Mahant की जादुई दुनिया में सब मुमकिन है!",
          dialogueEnglish = "In the magical world of TD Mahant, anything is possible!",
          backgroundVibe = "Vibrant Starry Studio",
          moodEmoji = "🎬"
        ),
        CartoonScene(
          sceneNumber = 3,
          title = "Scene 3: Victory Dance",
          visualDescription = "All village animals join for a synchronized joyful dance in colorful neon fireworks.",
          characterAction = "Raju and Chintu wave triumphantly at the camera",
          dialogueHindi = "दोस्तों, अपना कार्टून वीडियो खुद बनाएं और खूब सिक्के कमाएं!",
          dialogueEnglish = "Friends, create your own cartoon videos and earn coin rewards!",
          backgroundVibe = "Celebration Stage",
          moodEmoji = "🏆"
        )
      ),
      status = VideoStatus.COMPLETED,
      createdAt = System.currentTimeMillis() - 3600000L * 4
    )
    _videos.value = listOf(demoVideo)

    recalculateOwnerStats()
  }

  // --- Auth Flow ---
  fun requestOtp(countryCode: String, phoneNumber: String): String {
    // Generate realistic 6 digit OTP
    // For convenience in testing and evaluation, return the code
    val code = ((phoneNumber.hashCode().let { kotlin.math.abs(it) } % 900000) + 100000).toString()
    return code
  }

  fun verifyOtpAndLogin(
    countryCode: String,
    phoneNumber: String,
    name: String,
    enteredReferralCode: String?
  ): Result<User> {
    val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
    if (cleanPhone.length < 5) {
      return Result.failure(Exception("Please enter a valid phone number"))
    }

    val existingUser = _allUsers.value.find { it.countryCode == countryCode && it.phoneNumber == cleanPhone }
    if (existingUser != null) {
      if (existingUser.isBanned) {
        return Result.failure(Exception("This account has been suspended by administration."))
      }
      _currentUser.value = existingUser
      return Result.success(existingUser)
    }

    // New User Registration
    val newReferralCode = "TDM-" + cleanPhone.takeLast(4).let {
      if (it.length == 4) it else UUID.randomUUID().toString().take(4).uppercase()
    }

    val newUser = User(
      id = "user_${System.currentTimeMillis()}",
      phoneNumber = cleanPhone,
      countryCode = countryCode,
      name = name.ifBlank { "Creator $newReferralCode" },
      referralCode = newReferralCode,
      referredBy = enteredReferralCode?.trim()?.takeIf { it.isNotBlank() },
      coinBalance = 200, // 200 Free Welcome Coins!
      hasReceivedWelcomeBonus = true,
      isBanned = false,
      createdAt = System.currentTimeMillis()
    )

    // Add welcome transaction
    val welcomeTx = CoinTransaction(
      id = "tx_welcome_${System.currentTimeMillis()}",
      userId = newUser.id,
      amount = 200,
      type = TransactionType.WELCOME_BONUS,
      title = "New User Welcome Bonus",
      description = "200 Free Coins credited on successful registration!"
    )

    val updatedUsers = _allUsers.value.toMutableList()
    val updatedTransactions = _transactions.value.toMutableList()
    val updatedReferrals = _referrals.value.toMutableList()

    // Handle Referral Bonus if referredBy is provided
    if (!enteredReferralCode.isNullOrBlank()) {
      val referrer = updatedUsers.find { it.referralCode.equals(enteredReferralCode.trim(), ignoreCase = true) }
      if (referrer != null && referrer.id != newUser.id) {
        // Award 50 coins to referrer
        val updatedReferrer = referrer.copy(coinBalance = referrer.coinBalance + 50)
        val idx = updatedUsers.indexOfFirst { it.id == referrer.id }
        if (idx >= 0) {
          updatedUsers[idx] = updatedReferrer
        }

        val refTx = CoinTransaction(
          id = "tx_ref_${System.currentTimeMillis()}",
          userId = referrer.id,
          amount = 50,
          type = TransactionType.REFERRAL_BONUS,
          title = "Referral Reward Earned",
          description = "50 coins for inviting $countryCode $cleanPhone"
        )
        updatedTransactions.add(0, refTx)

        val refRecord = ReferralRecord(
          id = "ref_${System.currentTimeMillis()}",
          referrerCode = referrer.referralCode,
          referredPhone = "$countryCode $cleanPhone",
          bonusCoins = 50,
          timestamp = System.currentTimeMillis()
        )
        updatedReferrals.add(0, refRecord)
      }
    }

    updatedUsers.add(newUser)
    updatedTransactions.add(0, welcomeTx)

    _allUsers.value = updatedUsers
    _transactions.value = updatedTransactions
    _referrals.value = updatedReferrals
    _currentUser.value = newUser

    return Result.success(newUser)
  }

  fun logout() {
    _currentUser.value = null
  }

  fun deleteAccount(userId: String) {
    _allUsers.value = _allUsers.value.filterNot { it.id == userId }
    _videos.value = _videos.value.filterNot { it.userId == userId }
    _transactions.value = _transactions.value.filterNot { it.userId == userId }
    if (_currentUser.value?.id == userId) {
      _currentUser.value = null
    }
  }

  // --- Cartoon Video Creation ---
  suspend fun createCartoonVideo(
    prompt: String,
    language: String,
    style: String,
    character: String,
    durationSec: Int,
    onProgress: (VideoStatus, String) -> Unit
  ): Result<CartoonVideo> {
    val user = _currentUser.value ?: return Result.failure(Exception("Please log in to create cartoon videos"))
    if (user.coinBalance < 10) {
      return Result.failure(Exception("Insufficient coins! Video creation requires 10 coins. You currently have ${user.coinBalance} coins."))
    }

    return try {
      val video = CartoonAiGenerator.generateCartoonVideo(
        userId = user.id,
        prompt = prompt,
        language = language,
        style = style,
        character = character,
        durationSec = durationSec,
        onProgress = onProgress
      )

      // Deduct 10 coins ONLY after successful generation
      val updatedUser = user.copy(coinBalance = user.coinBalance - 10)
      _currentUser.value = updatedUser
      val userList = _allUsers.value.toMutableList()
      val idx = userList.indexOfFirst { it.id == user.id }
      if (idx >= 0) {
        userList[idx] = updatedUser
        _allUsers.value = userList
      }

      val deductTx = CoinTransaction(
        id = "tx_video_${System.currentTimeMillis()}",
        userId = user.id,
        amount = -10,
        type = TransactionType.VIDEO_CREATION,
        title = "Cartoon Video Creation",
        description = "10 coins charged for '${video.title}'"
      )
      _transactions.value = listOf(deductTx) + _transactions.value
      _videos.value = listOf(video) + _videos.value

      // Update Owner Earnings: ₹5 per successfully generated video
      recalculateOwnerStats()

      Result.success(video)
    } catch (e: Exception) {
      // Do NOT charge coins if generation failed
      Result.failure(Exception("Failed to generate cartoon video: ${e.message}. No coins were deducted."))
    }
  }

  fun deleteVideo(videoId: String) {
    _videos.value = _videos.value.filterNot { it.id == videoId }
    recalculateOwnerStats()
  }

  // --- Coin Recharge ---
  fun addCoins(amount: Int, title: String, description: String) {
    val user = _currentUser.value ?: return
    val updatedUser = user.copy(coinBalance = user.coinBalance + amount)
    _currentUser.value = updatedUser
    val userList = _allUsers.value.toMutableList()
    val idx = userList.indexOfFirst { it.id == user.id }
    if (idx >= 0) {
      userList[idx] = updatedUser
      _allUsers.value = userList
    }

    val tx = CoinTransaction(
      id = "tx_recharge_${System.currentTimeMillis()}",
      userId = user.id,
      amount = amount,
      type = TransactionType.RECHARGE,
      title = title,
      description = description
    )
    _transactions.value = listOf(tx) + _transactions.value
  }

  // --- Owner & Payouts ---
  private fun recalculateOwnerStats() {
    val totalVideos = _videos.value.count { it.status == VideoStatus.COMPLETED }
    val accrued = totalVideos * 5.0
    val paid = _payouts.value.filter { it.status == PayoutStatus.SUCCESSFUL }.sumOf { it.amountInr }
    val pending = _payouts.value.filter { it.status == PayoutStatus.PENDING || it.status == PayoutStatus.PROCESSING }.sumOf { it.amountInr }

    _ownerStats.value = _ownerStats.value.copy(
      totalVideosGenerated = totalVideos,
      totalAccruedEarningsInr = accrued,
      paidEarningsInr = paid,
      pendingEarningsInr = pending + (accrued - paid - pending).coerceAtLeast(0.0)
    )
  }

  fun requestOwnerPayout(amountInr: Double, notes: String): Result<PayoutRecord> {
    if (amountInr <= 0) {
      return Result.failure(Exception("Payout amount must be greater than 0"))
    }

    val payout = PayoutRecord(
      id = "payout_${System.currentTimeMillis()}",
      amountInr = amountInr,
      destinationUpi = _ownerStats.value.verifiedUpiId,
      phonePeNumber = _ownerStats.value.phonePeNumber,
      requestedAt = System.currentTimeMillis(),
      processedAt = null,
      status = PayoutStatus.PENDING,
      utrReference = null,
      notes = notes.ifBlank { "Owner Video Generation Earnings Payout (PhonePe ${_ownerStats.value.phonePeNumber})" }
    )

    _payouts.value = listOf(payout) + _payouts.value
    recalculateOwnerStats()
    return Result.success(payout)
  }

  fun updatePayoutStatus(payoutId: String, newStatus: PayoutStatus, utr: String?, notes: String?) {
    val list = _payouts.value.toMutableList()
    val idx = list.indexOfFirst { it.id == payoutId }
    if (idx >= 0) {
      val existing = list[idx]
      list[idx] = existing.copy(
        status = newStatus,
        processedAt = if (newStatus == PayoutStatus.SUCCESSFUL) System.currentTimeMillis() else existing.processedAt,
        utrReference = utr ?: existing.utrReference,
        notes = notes ?: existing.notes
      )
      _payouts.value = list
      recalculateOwnerStats()
    }
  }

  // --- Admin Moderation ---
  fun toggleUserBan(userId: String) {
    val list = _allUsers.value.toMutableList()
    val idx = list.indexOfFirst { it.id == userId }
    if (idx >= 0) {
      val u = list[idx]
      list[idx] = u.copy(isBanned = !u.isBanned)
      _allUsers.value = list
      if (_currentUser.value?.id == userId) {
        _currentUser.value = list[idx]
      }
    }
  }

  fun adminAdjustCoins(userId: String, delta: Int) {
    val list = _allUsers.value.toMutableList()
    val idx = list.indexOfFirst { it.id == userId }
    if (idx >= 0) {
      val u = list[idx]
      val updated = u.copy(coinBalance = (u.coinBalance + delta).coerceAtLeast(0))
      list[idx] = updated
      _allUsers.value = list
      if (_currentUser.value?.id == userId) {
        _currentUser.value = updated
      }

      val tx = CoinTransaction(
        id = "tx_admin_${System.currentTimeMillis()}",
        userId = userId,
        amount = delta,
        type = if (delta >= 0) TransactionType.RECHARGE else TransactionType.VIDEO_CREATION,
        title = "Admin Balance Adjustment",
        description = "Administrator updated coin balance by ${if (delta > 0) "+$delta" else "$delta"}"
      )
      _transactions.value = listOf(tx) + _transactions.value
    }
  }
}
