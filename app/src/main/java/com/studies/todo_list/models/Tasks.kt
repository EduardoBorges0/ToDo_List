package com.studies.todo_list.models

data class Tasks(
    val task : String,
    var isCompleted: Boolean = false
)