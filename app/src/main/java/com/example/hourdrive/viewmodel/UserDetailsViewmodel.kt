package com.example.hourdrive.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourdrive.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _muser = MutableStateFlow<MUser?>(null)  // Initial state is null
    val muser: StateFlow<MUser?> = _muser


    private val db = FirebaseFirestore.getInstance()

    fun getUserDetails(userId: String,onSuccess:()->Unit={}) {
        viewModelScope.launch {

        db.collection("users").document(userId).addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val mUser = snapshot.toObject(MUser::class.java)
                _muser.value=mUser

            }
        }

    }
}
    fun clearUserData() {
        _muser.value = null
    }

}
