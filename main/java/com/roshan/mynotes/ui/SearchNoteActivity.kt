package com.roshan.mynotes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.roshan.mynotes.Constants
import com.roshan.mynotes.R
import com.roshan.mynotes.callbacks.OnNotesItemClickListener
import com.roshan.mynotes.localDb.RealmMethods
import com.roshan.mynotes.localDb.realmModels.NoteModel
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_search_note.*
import java.util.*

class SearchNoteActivity : AppCompatActivity(), OnNotesItemClickListener {

    private var adapter : NotesRecyclerViewAdapter? = null
    private lateinit var dataset : RealmResults<NoteModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_note)
        setUpUi()
    }

    private fun setUpUi() {
        queryText.addTextChangedListener(textWatcher)
        val realmInstance = Realm.getDefaultInstance()
        dataset = realmInstance.where(NoteModel::class.java).findAll()
        adapter = NotesRecyclerViewAdapter(this, dataset, this, false)
        searchedNotes.adapter = adapter
        searchedNotes.layoutManager = LinearLayoutManager(this)
        searchNoteToolBar.setNavigationOnClickListener { finish() }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.d("MyTest", "Text Changed")
            dataset = RealmMethods.getNotesGivenQuery(s.toString())
            adapter = NotesRecyclerViewAdapter(this@SearchNoteActivity, dataset, this@SearchNoteActivity, false)
            searchedNotes.adapter = adapter
        }

    }

    override fun onItemClick(clickedNoteId: Int) {
        val callNoteActivityIntent = Intent(this, NoteActivity::class.java)
        callNoteActivityIntent.putExtra(Constants.CLICK_IDENTIFIER, Constants.LIST_VIEW_CLICK)
        callNoteActivityIntent.putExtra(Constants.NOTE_ID, clickedNoteId)
        startActivity(callNoteActivityIntent)
    }

}