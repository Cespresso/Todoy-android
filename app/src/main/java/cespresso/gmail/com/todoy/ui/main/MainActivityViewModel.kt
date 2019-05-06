package cespresso.gmail.com.todoy.ui.main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cespresso.gmail.com.todoy.await
import cespresso.gmail.com.todoy.data.entity.Todo
import cespresso.gmail.com.todoy.data.source.remote.ITodoyApiService
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.TaskState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val api: ITodoyApiService
) : ViewModel() {

//    private val viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val user = MutableLiveData<FirebaseAuth>()

    val todos = MutableLiveData<MutableList<Todo>>().apply {
        value = mutableListOf()
    }

    val todoRefreshState = MutableLiveData<Event<TaskState>>()

    val todoAddState = MutableLiveData<Event<TaskState>>()

    val todoDeleteState = MutableLiveData<Event<TaskState>>()

    val todoEditState = MutableLiveData<Event<TaskState>>()

    val toggleTodoCompleteState = MutableLiveData<Event<TaskState>>()


    val loginEvent = MutableLiveData<Event<Unit>>()

    val logoutEvent = MutableLiveData<Event<Unit>>()

    val makeSnackBarEvent = MutableLiveData<Event<String>>()

    val periodicSynchronizationPref = MutableLiveData<Boolean>()

    // プログレスバーを表示するためのローディングスタック
    val loadingEventStack = MutableLiveData<MutableList<String>>()

    fun loginTask() {
        loginEvent.value = Event(Unit)
    }

    fun logoutTask() {
        logoutEvent.value = Event(Unit)
    }

    fun refreshAllTodoByRemote() {
        val firebaseUser = user.value?.currentUser ?: return
        viewModelScope.launch {
            todoRefreshState.value = Event(TaskState.Progress)
            todoRefreshState.value = try {
                val task = firebaseUser.getIdToken(true).await()
                val todos_result = api.getAllTodo("Bearer " + task.token).await()
                if (todos_result.isSuccessful) {
                    todos.value?.clear()
                    // ひとまずIDでソート出力
                    val sortedTodo = todos_result.body()!!.sortedBy { it.id }
//                    todos.value?.addAll(sortedTodo) こっちだと変更通知が走らない
                    todos.value = sortedTodo.toMutableList()
                } else {
                    throw Exception("サーバーからエラーを返されました")
                }
                Event(TaskState.Complete(null))
            } catch (e: Exception) {
                Event(TaskState.Figure(e))
            }
        }
    }

    fun saveTodoTask(todo: Todo) {
        val firebaseUser = user.value?.currentUser ?: return
        viewModelScope.launch {
            todoAddState.value = Event(TaskState.Progress)
            todoAddState.value = try {
                val task = firebaseUser.getIdToken(true).await()
                val result = api.postTodo("Bearer " + task.token, todo).await()
                if (result.isSuccessful) {
                    Event(TaskState.Complete(null))
                } else {
                    Event(TaskState.Figure(Exception("todoの追加に失敗しました")))
                }
            } catch (e: Exception) {
                Event(TaskState.Figure(Exception("todoの追加に失敗しました")))
            }
        }
    }


    fun getServerStatusTask() {
        viewModelScope.launch {
            try {
                val result = api.getServerStatus().await()
                if (result.isSuccessful) {
//                    result.body()?.let {
//                        makeSnackBarEvent.value = Event()
//                    }
                } else {
                    Exception("Todoサーバーとの接続に失敗しました通信状態をお確かめください")
                }
            } catch (e: Exception) {
                makeSnackBarEvent.value = Event("Todoサーバーとの接続に失敗しました通信状態をお確かめください")
            }
        }
    }

    fun deleteTodoTask(id: Int) {
        val firebaseUser = user.value?.currentUser
        if (firebaseUser == null) {
            makeSnackBarEvent.value = Event("削除に失敗しました。ログイン状態をお確かめいただき再度お願いします。")
            return
        }
        viewModelScope.launch {
            todoDeleteState.value = Event(TaskState.Progress)
            todoDeleteState.value = try {
                val task = firebaseUser.getIdToken(true).await()
                val result = api.deleteTodo("Bearer " + task.token, id).await()
                if (result.isSuccessful) {
                    Event(TaskState.Complete(null))
                } else {
                    Event(TaskState.Figure(Exception("todoの追加に失敗しました")))
                }
            } catch (e: Exception) {
                Event(TaskState.Figure(Exception("todoの追加に失敗しました")))
            }
        }
    }

    fun editTodoTask(todo: Todo) {
        val firebaseUser = user.value?.currentUser
        if (firebaseUser == null) {
            makeSnackBarEvent.value = Event("削除に失敗しました。ログイン状態をお確かめいただき再度お願いします。")
            return
        }
        viewModelScope.launch {
            todoEditState.value = Event(TaskState.Progress)
            todoEditState.value = try {
                val task = firebaseUser.getIdToken(true).await()
                val result = api.editTodo("Bearer " + task.token, todo.id!!, todo).await()
                if (result.isSuccessful) {
                    Event(TaskState.Complete(null))
                } else {
                    Event(TaskState.Figure(Exception("todoの更新に失敗しました")))
                }
            } catch (e: Exception) {
                Event(TaskState.Figure(Exception("todoの更新に失敗しました")))
            }
        }
    }

    fun toggleTodoCompleteTask(todo: Todo, completed: Boolean) {
        val firebaseUser = user.value?.currentUser
        if (firebaseUser == null) {
            makeSnackBarEvent.value = Event("todoの更新に失敗しました。ログイン状態をお確かめいただき再度お願いします。")
            return
        }
        val oldTodo = todo.copy()
        val newTodo = todo.copy(completed = completed)
        todos.value = todos.value!!.map { if (newTodo.id == it.id) newTodo else it }.toMutableList()
        viewModelScope.launch {
            toggleTodoCompleteState.value = Event(TaskState.Progress)
            toggleTodoCompleteState.value = try {
                val task = firebaseUser.getIdToken(true).await()
                val result = api.editTodo("Bearer " + task.token, todo.id!!, newTodo).await()
                if (result.isSuccessful) {
                    Event(TaskState.Complete(null))
                } else {
                    Event(TaskState.Figure(Exception("todoの更新に失敗しました")))
                }
            } catch (e: Exception) {
                // 更新に失敗した場合昔のTodoの状態に戻す
                todos.value = todos.value!!.map { if (newTodo.id == it.id) oldTodo else it }.toMutableList()
                Event(TaskState.Figure(Exception("todoの更新に失敗しました")))
            }
        }
    }

    fun startSynchronizationWorker() {
        makeSnackBarEvent.value = Event("現在この機能は実装されていません")
    }

    fun stopSynchronizationWorker() {
        makeSnackBarEvent.value = Event("現在この機能は実装されていません")
    }

//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//    }
}