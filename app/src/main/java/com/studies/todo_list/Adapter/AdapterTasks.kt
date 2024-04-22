import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studies.todo_list.R
import com.studies.todo_list.datas.Sqlite
import com.studies.todo_list.models.Tasks

class AdapterTasks(private val context: Context, val tasks: MutableList<Tasks>) :
    RecyclerView.Adapter<AdapterTasks.TasksHolder>() {
    var isClickable = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.list_todo, parent, false)

        return TasksHolder(layout)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {
        val task = tasks[position]
        holder.task.text = task.task
        toggleStrikeThrough(holder.task, task.isCompleted)

        holder.task.isClickable = isClickable

        holder.task.setOnClickListener {
            if (isClickable) {
                task.isCompleted = !task.isCompleted
                toggleStrikeThrough(holder.task, task.isCompleted)
                saveTaskState(task)
            }
        }
    }


    inner class TasksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.findViewById(R.id.checkBox)
    }

    private fun toggleStrikeThrough(textView: TextView, isCompleted: Boolean) {
        val paint = textView.paintFlags
        if (isCompleted) {
            textView.paintFlags = paint or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = paint and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun saveTaskState(task: Tasks) {

            val db = Sqlite(context).writableDatabase
            val values = ContentValues().apply {
                put("task", task.task)
                put("is_completed", if (task.isCompleted) 1 else 0)
            }
            db.update("tasks", values, "task = ?", arrayOf(task.task))

    }


}
