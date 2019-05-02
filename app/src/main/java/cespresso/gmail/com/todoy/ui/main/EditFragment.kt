package cespresso.gmail.com.todoy.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.data.entity.Todo
import cespresso.gmail.com.todoy.di.Injectable
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.TaskState
import kotlinx.android.synthetic.main.edit_todo_fragment.*
import javax.inject.Inject

class EditFragment : Fragment(), Injectable {

    lateinit var viewModel: MainActivityViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var todoId: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val safeArgs = EditFragmentArgs.fromBundle(arguments)
        todoId = safeArgs.todoId

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainActivityViewModel::class.java)

        return inflater.inflate(R.layout.edit_todo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (todoId != null) {
            val todo = viewModel.todos.value!!.first { it.id == todoId }
            todo_edit_title.editText?.setText(todo.title, TextView.BufferType.NORMAL)
            todo_edit_body.editText?.setText(todo.body, TextView.BufferType.NORMAL)
            todo_edit_save_button.setOnClickListener {
                val todos = viewModel.todos.value
                val todo: Todo? = todos?.find { it.id == todoId }
                if (todo != null) {
                    todo.title = todo_edit_title.editText?.text.toString()
                    todo.body = todo_edit_body.editText?.text.toString()
                    viewModel.editTodoTask(todo)
                }
            }
        }
        viewModel.todoEditState.observe(this@EditFragment, Observer {
            it.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is TaskState.Progress -> {
                        todo_edit_progressbar.visibility = View.VISIBLE
                        todo_edit_title.isEnabled = false
                        todo_edit_body.isEnabled = false
                        todo_edit_save_button.isEnabled = false
                    }
                    is TaskState.Figure -> {
                        viewModel.makeSnackBarEvent.value = Event("Todoの更新に失敗しました。")
                        todo_edit_progressbar.visibility = View.INVISIBLE
                        todo_edit_title.isEnabled = true
                        todo_edit_body.isEnabled = true
                        todo_edit_save_button.isEnabled = true
                    }
                    is TaskState.Complete<*> -> {
                        viewModel.makeSnackBarEvent.value = Event("Todoの更新に成功しました。")
                        findNavController().popBackStack()
                    }
                    else -> {
                    }
                }
            }
        })
    }
}
