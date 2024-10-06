package com.example.hourdrive.viewmodel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourdrive.model.MUser
import com.example.hourdrive.model.muserToMap
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthViewmodel:ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()
    val loading= mutableStateOf(false)

    fun sendVerificationCode(phoneNumber: String,activity:Activity) {
        loading.value=true
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                    loading.value=false
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _authState.value = AuthState.Error(e.message ?: "Verification failed")
                    loading.value=false
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _authState.value = AuthState.CodeSent(verificationId)
                    loading.value=false
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun verifyCode(verificationId: String, code: String) {
        loading.value=true
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        loading.value=true
        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                if (user != null) {
                    val userProfile = getUserProfile(user.uid)
                    if (userProfile != null) {

                            _authState.value = AuthState.ExistingUser(userProfile)

                    } else {
                        Log.d("1111", user!!.uid)
                        _authState.value = AuthState.NewUser(user.uid)
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to get user information")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }finally {
                loading.value=false
            }
        }

    }

    fun completeUserProfile(mUser: MUser,userId:String) {
        loading.value=true
        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId)
                    .set(
                        muserToMap(mUser)
                    ).await()
                _authState.value = AuthState.ProfileCompleted
            } catch (e: Exception) {
                _authState.value = AuthState.Error(userId+ e.message + "Failed to complete profile")
            }finally {
                loading.value=false
            }
        }

    }

    private suspend fun getUserProfile(userId: String): MUser? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.toObject(MUser::class.java)
            } else {
                null
            }
        } catch (e: Exception) {

            null
        }
    }
    fun logout(){
        viewModelScope.launch {
            FirebaseAuth.getInstance().signOut()
        }
    }


}





sealed class AuthState {
    object Idle : AuthState()
    data class CodeSent(val verificationId: String) : AuthState()
    data class NewUser(val userId: String) : AuthState()
    data class ExistingUser(val profile: MUser) : AuthState()
    object ProfileCompleted : AuthState()
    data class Error(val message: String) : AuthState()
}