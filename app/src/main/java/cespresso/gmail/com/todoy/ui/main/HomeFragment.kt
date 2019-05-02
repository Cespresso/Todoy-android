package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.di.Injectable
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.TaskState
import cespresso.gmail.com.todoy.ui.TodosAdapter
import cespresso.gmail.com.todoy.ui.WrapContentLinearLayoutManager
import kotlinx.android.synthetic.main.home_fragment_todo_list.*
import javax.inject.Inject

class HomeFragment : Fragment(), Injectable {

    lateinit var viewModel: MainActivityViewModel
    lateinit var mLinearLayoutManager: LinearLayoutManager
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var todosAdapter: TodosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainActivityViewModel::class.java)
        todosAdapter = TodosAdapter(
            { item ->
                val action = HomeFragmentDirections.actionMainFragmentToShowFragment(item.id!!)
                findNavController().navigate(action)
            }, { todo, isChecked ->
                // todoをisCheckedの値を基に変更
                todo.completed = isChecked
                viewModel.editTodoTask(todo)
            }
        )

        mLinearLayoutManager = WrapContentLinearLayoutManager(container?.context!!)
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val buttonLogout = view.findViewById<Button>(R.id.button2)
//        buttonLogout.setOnClickListener{
//            findNavController().navigate(R.id.action_global_loginFragment)
//        }

        val list = view.findViewById<RecyclerView>(R.id.todo_list)
        val itemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(itemDecoration)
        list.layoutManager = mLinearLayoutManager
        list.adapter = todosAdapter
        viewModel.todos.observe(this@HomeFragment, Observer { todo ->
            val adapter = list.adapter
            if (adapter is TodosAdapter) {
                adapter.setTodo(todo)
//                adapter.notifyDataSetChanged()
            }
        })
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.refreshAllTodoByRemote()
        }
        viewModel.todoRefreshState.observe(this@HomeFragment, Observer<Event<TaskState>> {
            it.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is TaskState.Progress -> swipe_refresh_layout.isRefreshing = true
                    is TaskState.Figure -> {
                        swipe_refresh_layout.isRefreshing = false
                        viewModel.makeSnackBarEvent.value = Event("Todoの更新に失敗しました")
                    }
                    is TaskState.Complete<*> -> {
                        swipe_refresh_layout.isRefreshing = false
                    }
                    else -> swipe_refresh_layout.isRefreshing = false
                }
            }
        })
        viewModel.todoEditState.observe(this@HomeFragment, Observer {
            it.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is TaskState.Progress -> {
                    }
                    is TaskState.Figure -> {
                        //TODO checkboxがそのままになってしまう
                        // 独自にException classを定義して失敗したtodoのviewだけそれを基に変更する必要がありそう
                        viewModel.makeSnackBarEvent.value = Event("todoの更新に失敗しました")
                    }
                    is TaskState.Complete<*> -> {
                        // todoの更新に成功したらリフレッシュ
                        viewModel.refreshAllTodoByRemote()
                    }
                    else -> {
                    }
                }
            }
        })
//        viewModel.user.observe(this@MainActivity, Observer {user->
//            when(user){
//                null ->{
//                    home_loading.visibility = View.INVISIBLE
//                    home_login.visibility = View.INVISIBLE
//                    home_todo_list.visibility = View.INVISIBLE
//                }
//                else->{
//                    home_loading.visibility = View.INVISIBLE
//                    home_login.visibility = View.INVISIBLE
//                    home_todo_list.visibility = View.INVISIBLE
//                }
//            }
//        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshAllTodoByRemote()
    }
}