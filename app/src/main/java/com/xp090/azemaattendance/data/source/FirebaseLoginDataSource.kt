package com.xp090.azemaattendance.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xp090.azemaattendance.data.model.Credentials
import io.reactivex.Single

/**
 * Class that handles authentication w/ login credentials and retrieves userData information.
 */
class FirebaseLoginDataSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser:FirebaseUser? = firebaseAuth.currentUser
        private set


    fun login(credentials: Credentials): Single<Unit> {
        return Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(credentials.username!!, credentials.password!!)
                .addOnSuccessListener {
                    firebaseUser = it.user
                    emitter.onSuccess(Unit)
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        }


    }

    fun logout() {
        firebaseAuth.signOut()
    }
}

