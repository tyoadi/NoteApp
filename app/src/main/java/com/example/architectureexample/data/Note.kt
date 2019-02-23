package com.example.architectureexample.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_app")
data class Note(
    var title: String,
    var desc: String,
    var priority: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}