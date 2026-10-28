package com.example.data.model

data class User(
  val id: String,
  val phoneNumber: String,
  val countryCode: String,
  val name: String,
  val referralCode: String,
  val referredBy: String? = null,
  val coinBalance: Int = 200,
  val hasReceivedWelcomeBonus: Boolean = true,
  val isBanned: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

data class CartoonScene(
  val sceneNumber: Int,
  val title: String,
  val visualDescription: String,
  val characterAction: String,
  val dialogueHindi: String,
  val dialogueEnglish: String,
  val backgroundVibe: String = "Cartoon Studio",
  val moodEmoji: String = "🎬"
)

enum class VideoStatus {
  QUEUED,
  SCRIPTING,
  GENERATING_SCENES,
  COMPLETED,
  FAILED
}

data class CartoonVideo(
  val id: String,
  val userId: String,
  val title: String,
  val prompt: String,
  val language: String,
  val style: String,
  val character: String,
  val durationSec: Int,
  val scenes: List<CartoonScene>,
  val status: VideoStatus = VideoStatus.COMPLETED,
  val createdAt: Long = System.currentTimeMillis(),
  val coinCost: Int = 10,
  val shareCount: Int = 0,
  val downloadCount: Int = 0
)

enum class TransactionType {
  WELCOME_BONUS,
  REFERRAL_BONUS,
  VIDEO_CREATION,
  RECHARGE,
  REFUND
}

data class CoinTransaction(
  val id: String,
  val userId: String,
  val amount: Int,
  val type: TransactionType,
  val title: String,
  val description: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class ReferralRecord(
  val id: String,
  val referrerCode: String,
  val referredPhone: String,
  val bonusCoins: Int = 50,
  val timestamp: Long = System.currentTimeMillis()
)

enum class PayoutStatus {
  PENDING,
  PROCESSING,
  SUCCESSFUL,
  FAILED
}

data class PayoutRecord(
  val id: String,
  val amountInr: Double,
  val destinationUpi: String = "9399412716@ybl",
  val phonePeNumber: String = "9399412716",
  val requestedAt: Long = System.currentTimeMillis(),
  val processedAt: Long? = null,
  val status: PayoutStatus = PayoutStatus.PENDING,
  val utrReference: String? = null,
  val notes: String = ""
)

data class OwnerStats(
  val phonePeNumber: String = "9399412716",
  val verifiedUpiId: String = "9399412716@ybl",
  val ratePerVideoInr: Double = 5.0,
  val totalVideosGenerated: Int = 0,
  val totalAccruedEarningsInr: Double = 0.0,
  val paidEarningsInr: Double = 0.0,
  val pendingEarningsInr: Double = 0.0
)
