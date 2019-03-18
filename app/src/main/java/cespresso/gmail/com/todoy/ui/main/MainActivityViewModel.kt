package cespresso.gmail.com.todoy.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import cespresso.gmail.com.todoy.data.entity.Todo
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import cespresso.gmail.com.todoy.ui.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val api: ITodoyApiService
):  ViewModel(){

    val loginEvent = MutableLiveData<Event<Unit>>()

    // プログレスバーを表示するためのローディングスタック
    val loadingEventStack = MutableLiveData<MutableList<String>>()

    fun loginTask(){
        loginEvent.value = Event(Unit)
    }

    private suspend fun getAllTodoByRemote(){
        val result = api.getAllTodo().await()
        if(result.isSuccessful){
            result.body()?.let {
                Log.i("^v^",it.toString())
            }
        }else{
            result.errorBody()?.let {
                Log.i("^v^",it.string())
            }

        }
    }
}