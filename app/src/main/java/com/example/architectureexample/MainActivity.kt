package com.example.architectureexample

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.architectureexample.adapter.NoteAdapter
import com.example.architectureexample.data.Note
import com.example.architectureexample.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
    }

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_add_note.setOnClickListener {
            startActivityForResult(
                Intent(this, AddNoteActivity::class.java),
                ADD_NOTE_REQUEST
            )
        }

        recyler_view.layoutManager = LinearLayoutManager(this)
        recyler_view.setHasFixedSize(true)
        var adapter = NoteAdapter()
        recyler_view.adapter = adapter

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        noteViewModel.getAllNotes().observe(this, Observer<List<Note>> {
            adapter.submitList(it)
        })

        ItemTouchHelper(object  : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                noteViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                Toast.makeText(baseContext, "Note Deleted", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyler_view)

        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                var intent = Intent(baseContext, AddNoteActivity::class.java)
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.id)
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(AddNoteActivity.EXTRA_DESCRIPTION, note.desc)
                intent.putExtra(AddNoteActivity.EXTRA_PRIORITY, note.priority)
                startActivityForResult(intent, EDIT_NOTE_REQUEST)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.delete_all_note -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_LONG).show()
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val newNote = Note(data!!.getStringExtra(AddNoteActivity.EXTRA_TITLE),
                data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION),
                data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY,1 )
            )
            noteViewModel.insert(newNote)
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra(AddNoteActivity.EXTRA_ID, -1)

            if (id == -1) {
                Toast.makeText(this, " Cloud not update", Toast.LENGTH_SHORT).show()

            }

            val updateNote = Note(data!!.getStringExtra(AddNoteActivity.EXTRA_TITLE),
                data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION),
                data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, -1))
                noteViewModel.update(updateNote)
        } else {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show()
        }
    }
}
