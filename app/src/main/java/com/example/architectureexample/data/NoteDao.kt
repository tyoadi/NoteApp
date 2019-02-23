package com.example.architectureexample.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_app")
    fun deleteAllNotes()

    @Query("SELECT* FROM note_app ORDER BY priority DESC")
    fun getAllNotes(): LiveData<List<Note>>
}