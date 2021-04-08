package com.roshan.mynotes.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.roshan.mynotes.Constants
import com.roshan.mynotes.R
import com.roshan.mynotes.callbacks.OnNotesItemClickListener
import com.roshan.mynotes.localDb.realmModels.NoteModel
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_notes_list.*

class NotesListFragment : Fragment(), OnNotesItemClickListener {

    private lateinit var activity : AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpListView()
    }

    private fun setUpListView() {
        val realmInstance =  Realm.getDefaultInstance()
        val data = realmInstance.where(NoteModel::class.java).findAll()
        val adapter = NotesRecyclerViewAdapter(activity, data, this, true)
        notesListView.adapter = adapter
        notesListView.layoutManager = LinearLayoutManager(context)
    }

    override fun onItemClick(clickedNoteId: Int) {
        val callNoteActivityIntent = Intent(activity, NoteActivity::class.java)
        callNoteActivityIntent.putExtra(Constants.CLICK_IDENTIFIER, Constants.LIST_VIEW_CLICK)
        callNoteActivityIntent.putExtra(Constants.NOTE_ID, clickedNoteId)
        startActivity(callNoteActivityIntent)
    }


}