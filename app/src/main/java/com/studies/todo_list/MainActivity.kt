package com.studies.todo_list

import AdapterTasks
import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.studies.todo_list.databinding.ActivityMainBinding
import com.studies.todo_list.datas.Sqlite
import com.studies.todo_list.models.Tasks

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var inputText: EditText
    private lateinit var btnAddTask: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterTasks

    private var isOverlayVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        inputText = binding.inputText
        btnAddTask = binding.btnAddTask
        recyclerView = binding.recyclerView

        val tasksFromDB = Sqlite(this).getAllTasks().toMutableList()

        adapter = AdapterTasks(this, tasksFromDB)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnAdd.setOnClickListener {
            isOverlayVisible = !isOverlayVisible
            if (isOverlayVisible) {
                binding.grayLayer.visibility = View.VISIBLE
                inputText.visibility = View.VISIBLE
                btnAddTask.visibility = View.VISIBLE
                adapter.isClickable = false

            } else {
                binding.grayLayer.visibility = View.GONE
                inputText.visibility = View.GONE
                btnAddTask.visibility = View.GONE
                adapter.isClickable = true
            }
        }

        btnAddTask.setOnClickListener {
            val taskText = inputText.text.toString()
            if (taskText.isNotBlank()) {
                val db = Sqlite(this@MainActivity).writableDatabase
                val values = ContentValues().apply {
                    put("task", taskText)
                }
                val newRowId = db.insert("tasks", null, values)
                if (newRowId != -1L) {
                    val newTask = Tasks(taskText)
                    adapter.tasks.add(newTask)
                    adapter.notifyItemInserted(adapter.tasks.size - 1)
                    inputText.text.clear()
                }
            }
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedTask = adapter.tasks[position]

                val db = Sqlite(this@MainActivity).writableDatabase
                db.delete("tasks", "task = ?", arrayOf(removedTask.task))
                adapter.tasks.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
