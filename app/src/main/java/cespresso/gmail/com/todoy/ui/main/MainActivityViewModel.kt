package cespresso.gmail.com.todoy.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import cespresso.gmail.com.todoy.ui.Event
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class MainActivityViewModel @Inject constructor():  ViewModel(){
    val user =MutableLiveData<FirebaseUser>()

    val loginEvent = MutableLiveData<Event<Unit>>()

    fun loginTask(){
        loginEvent.value = Event(Unit)
    }
}