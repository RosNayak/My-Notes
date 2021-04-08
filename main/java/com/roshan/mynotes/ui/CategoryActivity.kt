package com.roshan.mynotes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.roshan.mynotes.Constants
import com.roshan.mynotes.R
import com.roshan.mynotes.callbacks.OnNotesItemClickListener
import com.roshan.mynotes.handler.PreferenceHandler
import com.roshan.mynotes.localDb.RealmMethods
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_note.*

class CategoryActivity : AppCompatActivity(), OnNotesItemClickListener {

    private val CATEGORY_KEY : String = "Category"
    private lateinit var sharedPreferences : PreferenceHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        sharedPreferences = PreferenceHandler(this)
        uiSetUp()
    }

    private fun uiSetUp() {
        notesListSetUp()
        toolBarSetUp()
    }

    private fun notesListSetUp() {
        var category = intent.getStringExtra(CATEGORY_KEY)
        category = category ?: Constants.DEFAULT_CATEGORY
        val notes = RealmMethods.getNotesGivenCategory(category)
        val adapter = NotesRecyclerViewAdapter(this, notes, this, true)
        categoryNotesList.adapter = adapter
        categoryNotesList.layoutManager = LinearLayoutManager(this)
    }

    private fun toolBarSetUp() {
        setSupportActionBar(categoryToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        categoryToolBarText.text = intent.getStringExtra(CATEGORY_KEY) ?: Constants.DEFAULT_CATEGORY
    }

    override fun onItemClick(clickedNoteId: Int) {
        val callNoteActivityIntent = Intent(this, NoteActivity::class.java)
        callNoteActivityIntent.putExtra(Constants.CLICK_IDENTIFIER, Constants.LIST_VIEW_CLICK)
        callNoteActivityIntent.putExtra(Constants.NOTE_ID, clickedNoteId)
        startActivity(callNoteActivityIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                true
            }
            R.id.format -> {

                true
            }
            R.id.sort -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}