package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.di.Injectable
import cespresso.gmail.com.todoy.ui.TodosAdapter
import javax.inject.Inject

class HomeFragment : Fragment(),Injectable{

    lateinit var viewModel:MainActivityViewModel
    lateinit var mLinearLayoutManager: LinearLayoutManager
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(activity!!,viewModelFactory).get(MainActivityViewModel::class.java)

        mLinearLayoutManager = LinearLayoutManager(container?.context)
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val buttonLogout = view.findViewById<Button>(R.id.button2)
//        buttonLogout.setOnClickListener{
//            findNavController().navigate(R.id.action_global_loginFragment)
//        }

        val list = view.findViewById<RecyclerView>(R.id.todo_list)
        list.layoutManager = mLinearLayoutManager
        list.adapter = TodosAdapter(viewModel.todos.value!!) { item->
            Log.i("^v^",item.body)
        }
        viewModel.todos.observe(this@HomeFragment, Observer {
            val adapter = list.adapter
            if(adapter is TodosAdapter){
                adapter.notifyDataSetChanged()
            }
        })
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshAllTodoByRemote()
        }
        viewModel.todoRefreshState.observe(this@HomeFragment, Observer<Boolean> {
            swipeRefreshLayout.isRefreshing = it
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
}