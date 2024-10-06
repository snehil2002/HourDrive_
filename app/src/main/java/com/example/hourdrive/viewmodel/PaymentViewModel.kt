package com.example.hourdrive.viewmodel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.razorpay.Checkout
import org.json.JSONObject

class PaymentViewModel : ViewModel() {

    private val _paymentSuccess = MutableLiveData<Boolean>()
    val paymentSuccess: LiveData<Boolean> = _paymentSuccess


    fun startPayment(activity: Activity, amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_3bB49k9EKzkgHb") // Replace this with your actual API key

        try {
            val options = createPaymentOptions(amount)
            checkout.open(activity, options)
        } catch (e: Exception) {
            showToast(activity, "Error in payment: ${e.message}")
            Log.e(TAG, "Error starting payment", e)
        }
    }

    private fun createPaymentOptions(amount: Int): JSONObject {
        return JSONObject().apply {
            put("name", "Hour Drive")
            put("description", "Booking Charges")
            put("theme.color", "#3399cc")
            put("currency", "INR")
            put("amount", (amount * 100).toString())
            put("prefill", JSONObject().apply {
                put("email", "hourDrive@noreply.com")
                put("contact", "9876543210")
            })
        }
    }

    fun onPaymentSuccess(razorpayPaymentId: String?) {
        _paymentSuccess.value = true
        Log.d(TAG, "Payment Successful: $razorpayPaymentId")
    }

    fun onPaymentError(code: Int, response: String?) {
        Log.e(TAG, "Payment error $code: $response")
    }

    fun resetPaymentSuccess() {
        _paymentSuccess.value = false
    }

    private fun showToast(activity: Activity, message: String) {
        Log.e(TAG, message)
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val TAG = "PaymentViewModel"
    }
}