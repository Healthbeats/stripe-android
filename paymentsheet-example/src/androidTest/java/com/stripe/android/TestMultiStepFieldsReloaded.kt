package com.stripe.android

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.stripe.android.test.core.AuthorizeAction
import com.stripe.android.test.core.Automatic
import com.stripe.android.test.core.Billing
import com.stripe.android.test.core.Currency
import com.stripe.android.test.core.Customer
import com.stripe.android.test.core.DelayedPMs
import com.stripe.android.test.core.GooglePayState
import com.stripe.android.test.core.IntentType
import com.stripe.android.test.core.LinkState
import com.stripe.android.test.core.PlaygroundTestDriver
import com.stripe.android.test.core.Shipping
import com.stripe.android.test.core.TestParameters
import com.stripe.android.ui.core.forms.resources.LpmRepository
import com.stripe.android.utils.TestRules
import com.stripe.android.utils.initializedLpmRepository
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestMultiStepFieldsReloaded {

    @get:Rule
    val rules = TestRules.create()

    private lateinit var device: UiDevice
    private lateinit var testDriver: PlaygroundTestDriver

    private val newUser = TestParameters(
        paymentMethod = lpmRepository.fromCode("bancontact")!!,
        customer = Customer.New,
        linkState = LinkState.Off,
        googlePayState = GooglePayState.Off,
        currency = Currency.EUR,
        intentType = IntentType.Pay,
        billing = Billing.Off,
        shipping = Shipping.Off,
        delayed = DelayedPMs.Off,
        automatic = Automatic.Off,
        saveCheckboxValue = false,
        saveForFutureUseCheckboxVisible = false,
        useBrowser = null,
        authorizationAction = AuthorizeAction.Authorize,
        merchantCountryCode = "GB",
    )

    @Before
    fun before() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        testDriver = PlaygroundTestDriver(device, rules.compose)
    }

    @Test
    fun testCard() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = LpmRepository.HardcodedCard,
                saveCheckboxValue = true,
                saveForFutureUseCheckboxVisible = true,
            )
        )
    }

    @Test
    fun testBancontact() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("bancontact")!!,
            )
        )
    }

    @Test
    fun testSepaDebit() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("sepa_debit")!!,
                delayed = DelayedPMs.On,
                authorizationAction = null
            ),
            populateCustomLpmFields = {
                rules.compose.onNodeWithText("IBAN").apply {
                    performTextInput(
                        "DE89370400440532013000"
                    )
                }
            },
            verifyCustomLpmFields = {
                rules.compose.onNodeWithText("IBAN").apply {
                    assertContentDescriptionEquals(
                        "DE89370400440532013000"
                    )
                }
            }

        )
    }

    @Test
    fun testIdeal() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("ideal")!!,
            )
        )
    }

    @Test
    fun testEps() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("eps")!!,
            )
        )
    }

    @Test
    fun testGiropay() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("giropay")!!,
            )
        )
    }

    @Test
    fun testP24() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("p24")!!,
            )
        )
    }

    @Test
    fun testAfterpay() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("afterpay_clearpay")!!,
                merchantCountryCode = "US",
                currency = Currency.USD,
                shipping = Shipping.On
            )
        )
    }

    @Test
    fun testAffirm() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("affirm")!!,
                merchantCountryCode = "US",
                currency = Currency.USD,
                shipping = Shipping.On
            )
        )
    }

    @Test
    fun testAuBecsDD() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("au_becs_debit")!!,
                delayed = DelayedPMs.On,
                merchantCountryCode = "AU",
                currency = Currency.AUD,
            )
        )
    }

    @Ignore("Complex authorization handling required")
    fun testKlarna() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("klarna")!!,
            )
        )
    }

    @Ignore("Need to add GBP currency to playground")
    fun testPayPal() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("paypal")!!,
            )
        )
    }

    @Test
    fun testCashAppPay() {
        testDriver.confirmCustom(
            newUser.copy(
                paymentMethod = lpmRepository.fromCode("cashapp")!!,
                currency = Currency.USD,
                merchantCountryCode = "US",
                authorizationAction = AuthorizeAction.Authorize,
                supportedPaymentMethods = listOf("card", "cashapp"),
            )
        )
    }

    companion object {
        // There exists only one screenshot processor so that all tests put
        // their files in the same directory.
        private val lpmRepository = initializedLpmRepository(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
        )
    }
}
