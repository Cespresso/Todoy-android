package cespresso.gmail.com.todoy.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.di.Injectable
import kotlinx.android.synthetic.main.show_todo_fragment.*
import javax.inject.Inject


class ShowFragment : Fragment(),Injectable {

    lateinit var viewModel:MainActivityViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var todoId:Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val safeArgs = ShowFragmentArgs.fromBundle(arguments)
        todoId = safeArgs.todoId
        viewModel = ViewModelProviders.of(activity!!,viewModelFactory).get(MainActivityViewModel::class.java)

        return inflater.inflate(R.layout.show_todo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(todoId != null){
            val todo = viewModel.todos.value!!.first { it.id == todoId }
            todo_show_title.text = todo.title
            todo_show_body.text = todo.body
        }

    }



}
