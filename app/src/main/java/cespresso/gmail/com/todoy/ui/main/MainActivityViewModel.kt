package cespresso.gmail.com.todoy.ui.main

import androidx.lifecycle.MutableLiveData


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cespresso.gmail.com.todoy.await
import cespresso.gmail.com.todoy.data.entity.Todo
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.TaskState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val api: ITodoyApiService
):  ViewModel(){

//    private val viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val user =MutableLiveData<FirebaseAuth>()

    val todos = MutableLiveData<MutableList<Todo>>().apply {
        value = mutableListOf()
    }

    val todoRefreshState = MutableLiveData<Event<TaskState>>()

    val todoAddState = MutableLiveData<Event<TaskState>>()

    val loginEvent = MutableLiveData<Event<Unit>>()

    val makeSnackBarEvent = MutableLiveData<Event<String>>()

    // プログレスバーを表示するためのローディングスタック
    val loadingEventStack = MutableLiveData<MutableList<String>>()

    fun loginTask(){
        loginEvent.value = Event(Unit)
    }
    fun refreshAllTodoByRemote(){
        val firebaseUser = user.value?.currentUser
        if(firebaseUser==null){
            makeSnackBarEvent.value = Event("登録に失敗しました。ログイン状態をお確かめいただき再度お願いします。")
            return
        }
        viewModelScope.launch {
            todoRefreshState.value  = Event(TaskState.Progress)
            todoRefreshState.value =  try{
                val task = firebaseUser.getIdToken(true).await()
                val todos_result = api.getAllTodo("Bearer "+task.token).await()
                if (todos_result.isSuccessful) {
                    todos.value?.clear()
                    // ひとまずIDでソート出力
                    val sortedTodo = todos_result.body()!!.sortedBy { it.id }
                    todos.value?.addAll(sortedTodo)
                }else{
                    throw Exception("サーバーからエラーを返されました")
                }
                Event(TaskState.Complete(null))
            }catch (e:Exception){
                Event(TaskState.Figure(e))
            }
        }
    }

    fun saveTodoTask(todo:Todo){
        val firebaseUser = user.value?.currentUser
        if(firebaseUser==null){
            makeSnackBarEvent.value = Event("登録に失敗しました。ログイン状態をお確かめいただき再度お願いします。")
            return
        }
        viewModelScope.launch {
            todoAddState.value  = Event(TaskState.Progress)
            todoAddState.value = try{
                val task = firebaseUser.getIdToken(true).await()
                val result = api.postTodo("Bearer "+task.token,todo).await()
                if(result.isSuccessful){
                    Event(TaskState.Complete(null))
                }else{
                    Event(TaskState.Figure(Exception("todoの追加に失敗しました")))
                }
            }catch (e:Exception){
                Event(TaskState.Figure(Exception("todoの追加に失敗しました")))
            }
        }
    }


    fun getServerStatusTask(){
        viewModelScope.launch {
            try{
                val result = api.getServerStatus().await()
                if(result.isSuccessful){
                    result.body()?.let {
                        makeSnackBarEvent.value = Event(it.string())
                    }
                }else{
                    Exception("Todoサーバーとの接続に失敗しました通信状態をお確かめください")
                }
            }catch (e:Exception){
                makeSnackBarEvent.value = Event("Todoサーバーとの接続に失敗しました通信状態をお確かめください")
            }
        }
    }


//    private suspend fun getAllTodoByRemote(){
//        val result = api.getAllTodo().await()
//        if(result.isSuccessful){
//            result.body()?.let {
//                Log.i("^v^",it.toString())
//            }
//        }else{
//            result.errorBody()?.let {
//                Log.i("^v^",it.string())
//            }
//
//        }
//    }
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
}