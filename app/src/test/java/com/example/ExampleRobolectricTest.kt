package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.AppRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TD Mahant Cartoon Videos", appName)
  }

  @Test
  fun `verify new user receives 200 welcome coins`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = AppRepository(context)

    val res = repository.verifyOtpAndLogin(
      countryCode = "+91",
      phoneNumber = "9876543210",
      name = "Test Creator",
      enteredReferralCode = null
    )

    assertTrue(res.isSuccess)
    val user = res.getOrNull()
    assertNotNull(user)
    assertEquals(200, user?.coinBalance)
  }

  @Test
  fun `verify referral gives 50 coins to referrer`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = AppRepository(context)

    // User A registers
    val userARes = repository.verifyOtpAndLogin("+91", "9111111111", "User A", null)
    val userA = userARes.getOrNull()!!
    assertEquals(200, userA.coinBalance)

    // User B registers with User A's referral code
    val userBRes = repository.verifyOtpAndLogin("+91", "9222222222", "User B", userA.referralCode)
    val userB = userBRes.getOrNull()!!
    assertEquals(200, userB.coinBalance)

    // Verify User A now has 200 + 50 = 250 coins
    val updatedUserA = repository.allUsers.value.find { it.id == userA.id }
    assertEquals(250, updatedUserA?.coinBalance)
  }

  @Test
  fun `verify owner earnings phonePe and video stats`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = AppRepository(context)

    assertEquals("9399412716", repository.ownerStats.value.phonePeNumber)
    assertEquals("9399412716@ybl", repository.ownerStats.value.verifiedUpiId)
    assertEquals(5.0, repository.ownerStats.value.ratePerVideoInr, 0.01)
  }
}
