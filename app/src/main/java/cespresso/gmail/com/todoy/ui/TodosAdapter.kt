package cespresso.gmail.com.todoy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cespresso.gmail.com.todoy.R
import cespresso.gmail.com.todoy.data.entity.Todo
import kotlinx.android.synthetic.main.todo_row.view.*

class TodosAdapter(private val todos:MutableList<Todo>,private val itemClickListener: (todo:Todo)->Unit) : RecyclerView.Adapter<TodosAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.todo_row,parent,false))
    }

    override fun getItemCount()= todos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos.getOrNull(position)
        todo?.let{
            holder.bind(it,itemClickListener)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titleText = itemView.findViewById<TextView>(R.id.list_todo_title)
        private val titleBody = itemView.findViewById<TextView>(R.id.list_todo_body)
        fun bind(todo: Todo,itemClickListener: (todo:Todo)->Unit){
            titleText.text = todo.title
            titleBody.text = todo.getShortBody()
            itemView.setOnClickListener {
                itemClickListener(todo)
            }
        }
    }
}