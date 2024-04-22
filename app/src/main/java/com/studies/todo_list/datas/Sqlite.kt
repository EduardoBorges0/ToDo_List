package com.studies.todo_list.datas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.studies.todo_list.models.Tasks

class Sqlite(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "task TEXT," +
                "is_completed INTEGER DEFAULT 0)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE tasks ADD COLUMN is_completed INTEGER DEFAULT 0")
        }
    }

    fun getAllTasks(): MutableList<Tasks> {
        val tasks = mutableListOf<Tasks>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT task, is_completed FROM tasks", null)
        with(cursor) {
            while (moveToNext()) {
                val taskText = getString(getColumnIndexOrThrow("task"))
                val isCompleted = getInt(getColumnIndexOrThrow("is_completed")) == 1
                tasks.add(Tasks(taskText, isCompleted))
            }
            close()
        }
        return tasks
    }
}
