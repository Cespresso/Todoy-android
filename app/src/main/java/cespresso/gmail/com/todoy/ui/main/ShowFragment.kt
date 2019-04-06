package cespresso.gmail.com.todoy.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.di.Injectable
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.TaskState
import cespresso.gmail.com.todoy.ui.YesOrNoDialogDirections
import kotlinx.android.synthetic.main.show_todo_fragment.*
import javax.inject.Inject


class ShowFragment : Fragment(),Injectable {

    lateinit var viewModel:MainActivityViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val DELETE_REQUEST_CODE = 1001

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
            todo_show_edit_button.setOnClickListener{
                val action  = ShowFragmentDirections.actionShowFragmentToEditFragment(todoId!!)
                findNavController().navigate(action)
            }
        }
        todo_show_delete_button.setOnClickListener {
            val action = YesOrNoDialogDirections.actionGlobalYesOrNoDialog("Delete","このTodoを削除しますか？",DELETE_REQUEST_CODE)
            findNavController().navigate(action)
        }
        viewModel.todoDeleteState.observe(this@ShowFragment, Observer {
            it.getContentIfNotHandled()?.let{state->
                when(state){
                    is TaskState.Progress ->{
                        todo_show_progressbar.visibility = View.VISIBLE
                        todo_show_title.isEnabled = false
                        todo_show_body.isEnabled = false
                        todo_show_edit_button.isEnabled = false
                        todo_show_delete_button.isEnabled = false
                    }
                    is TaskState.Figure->{
                        viewModel.makeSnackBarEvent.value = Event("Todoの削除に失敗しました。")
                        todo_show_progressbar.visibility = View.INVISIBLE
                        todo_show_title.isEnabled = true
                        todo_show_body.isEnabled = true
                        todo_show_edit_button.isEnabled = true
                        todo_show_delete_button.isEnabled = true
                    }
                    is TaskState.Complete<*>->{
                        viewModel.makeSnackBarEvent.value = Event("Todoの削除に成功しました。")
                        findNavController().popBackStack()
                    }
                    else ->{}
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            DELETE_REQUEST_CODE ->{
                if(resultCode == -1){
                    viewModel.deleteTodoTask(todoId!!)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}
