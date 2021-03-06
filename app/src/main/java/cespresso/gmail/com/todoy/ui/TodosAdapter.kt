package cespresso.gmail.com.todoy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.data.entity.Todo

class TodosAdapter(
    private val itemClickListener: (todo: Todo) -> Unit,
    private val checkBoxClickListener: (todo: Todo, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<TodosAdapter.ViewHolder>() {

    private val todos = mutableListOf<Todo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.todo_row, parent, false))
    }

    override fun getItemCount() = todos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos.getOrNull(position)
        todo?.let {
            holder.bind(it, itemClickListener, checkBoxClickListener)
        }
    }

    fun setTodo(new_todos: MutableList<Todo>) {
        if (todos.size == 0) {
            todos.clear()
            todos.addAll(new_todos)
            notifyItemRangeInserted(0, new_todos.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = todos.size
                override fun getNewListSize() = new_todos.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    todos[oldItemPosition].id!! == new_todos[newItemPosition].id!!

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    todos[oldItemPosition] == new_todos[newItemPosition]
            })
            todos.clear()
            todos.addAll(new_todos)
            result.dispatchUpdatesTo(this)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val todoTitle = itemView.findViewById<TextView>(R.id.list_todo_title)
        private val todoBody = itemView.findViewById<TextView>(R.id.list_todo_body)
        private val todoCheckBox = itemView.findViewById<CheckBox>(R.id.list_todo_checkBox)
        fun bind(
            todo: Todo,
            itemClickListener: (todo: Todo) -> Unit,
            checkBoxClickListener: (todo: Todo, isChecked: Boolean) -> Unit
        ) {
            if (todo.completed == true) {
                todoTitle.text =
                    HtmlCompat.fromHtml("<del>${todo.title.toString()}</del>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                todoBody.text =
                    HtmlCompat.fromHtml("<del>${todo.getShortBody()}</del>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                todoCheckBox.isChecked = true
                todoTitle.alpha = 0.6f
                todoBody.alpha = 0.6f
            } else {
                todoTitle.text = todo.title
                todoBody.text = todo.getShortBody()
                todoCheckBox.isChecked = false
                todoTitle.alpha = 1f
                todoBody.alpha = 1f
            }

            itemView.setOnClickListener {
                itemClickListener(todo)
            }
            todoCheckBox.setOnClickListener { view ->
                if (view is CompoundButton) {
                    checkBoxClickListener(todo, view.isChecked)
                }
            }
        }
    }
}