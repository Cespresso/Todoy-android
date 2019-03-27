package cespresso.gmail.com.todoy.ui.main

import androidx.lifecycle.MutableLiveData


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cespresso.gmail.com.todoy.data.entity.Todo
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import cespresso.gmail.com.todoy.ui.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val api: ITodoyApiService
):  ViewModel(){

//    private val viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val user =MutableLiveData<FirebaseAuth>()

    val loginEvent = MutableLiveData<Event<Unit>>()

    val makeSnackBarEvent = MutableLiveData<Event<String>>()

    // プログレスバーを表示するためのローディングスタック
    val loadingEventStack = MutableLiveData<MutableList<String>>()

    fun loginTask(){
        loginEvent.value = Event(Unit)
    }
    private fun refreshAllTodoByRemote(){
        viewModelScope.launch {
            val result = api.getAllTodo().await()
        }

    }


    fun getServerStatusTask(){
        viewModelScope.launch {
            val result = api.getServerStatus().await()
            if(result.isSuccessful){
                result.body()?.let {
                    makeSnackBarEvent.value = Event(it.string())
                }
            }else{
                result.errorBody()?.let {
                    Log.i("^v^",it.string())
                }

            }

        }
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
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
}