package cespresso.gmail.com.todoy.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.data.entity.Todo
import cespresso.gmail.com.todoy.di.Injectable
import cespresso.gmail.com.todoy.ui.Event
import cespresso.gmail.com.todoy.ui.TaskState
import kotlinx.android.synthetic.main.add_todo_fragment.*
import javax.inject.Inject


class AddFragment : Fragment() ,Injectable{

    lateinit var viewModel:MainActivityViewModel
    @Inject
    lateinit var viewModelFactory:ViewModelProvider.Factory
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!,viewModelFactory).get(MainActivityViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_todo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todo_add_save_button.setOnClickListener{
            val title:String = todo_add_title_text.editText?.text.toString()
            val body:String = todo_add_body_text.editText?.text.toString()
            val todo = Todo(title,body)
            viewModel.saveTodoTask(todo)
        }
        viewModel.todoAddState.observe(this@AddFragment, Observer<Event<TaskState>> {
            it.getContentIfNotHandled()?.let {state ->
                when(state){
                    is TaskState.Progress ->{
                        add_todo_progressbar.visibility = View.VISIBLE
                        todo_add_title_text.isEnabled = false
                        todo_add_body_text.isEnabled = false
                        todo_add_save_button.isEnabled = false
                    }
                    is TaskState.Figure->{
                        viewModel.makeSnackBarEvent.value = Event("Todoの追加に失敗しました。")
                        add_todo_progressbar.visibility = View.INVISIBLE
                        todo_add_title_text.isEnabled = true
                        todo_add_body_text.isEnabled = true
                        todo_add_save_button.isEnabled = true
                    }
                    is TaskState.Complete<*>->{
                        viewModel.makeSnackBarEvent.value = Event("Todoの追加に成功しました。")
                        findNavController().popBackStack()
                    }
                    else ->{}
                }
            }
        })
    }


}
