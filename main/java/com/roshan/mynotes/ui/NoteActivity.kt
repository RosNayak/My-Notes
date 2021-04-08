package com.roshan.mynotes.ui

import android.app.Notification
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.roshan.mynotes.Constants
import com.roshan.mynotes.R
import com.roshan.mynotes.callbacks.OnCategoryChooseListener
import com.roshan.mynotes.enums.NotePriority
import com.roshan.mynotes.handler.PreferenceHandler
import com.roshan.mynotes.localDb.RealmMethods
import com.roshan.mynotes.localDb.realmModels.NoteModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.new_category_dialog_layout.*
import kotlinx.android.synthetic.main.new_category_dialog_layout.view.*
import kotlinx.android.synthetic.main.no_note_details_dialog_layout.*
import kotlinx.android.synthetic.main.no_note_details_dialog_layout.view.*
import kotlinx.android.synthetic.main.note_details_dialog_layout.view.*
import kotlinx.android.synthetic.main.select_category_dialog_layout.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity(), OnCategoryChooseListener {

    private lateinit var sharedPreference : PreferenceHandler
    private var id : Int = -1
    private var newNote : Boolean = false
    private var priority : String? = NotePriority.DEFAULT_PRIORITY.value
    private var isNoteStarred : Boolean = false
    private var category : String = Constants.DEFAULT_CATEGORY
    private  val alertMessage : String = "Note not created. Please create the note to view these details."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        sharedPreference = PreferenceHandler(this)
        setUpUi()
    }

    private fun setUpUi() {
        setUpAppBar()
        setUpTextViews()
        setToolBarText()
        save.setOnClickListener(saveNoteButtonListener)

        toolBarTitle.setOnClickListener {
            val chooseCategoryDialog = AlertDialog.Builder(this).create()
            val view = layoutInflater.inflate(R.layout.select_category_dialog_layout, null)
            chooseCategoryDialog.setView(view)
            val categories = sharedPreference.getCategoriesList()
            Log.d("MyTest", "${categories?.size}")
            val adapter = if (categories == null) { CategoriesRecyclerView(this, ArrayList(), this) }
            else { CategoriesRecyclerView(this, categories, this) }
            view.categoriesList.adapter = adapter
            view.categoriesList.layoutManager = LinearLayoutManager(this)
            view.deleteCategory.setOnClickListener {
                category = Constants.DEFAULT_CATEGORY
                toolBarTitle.text = category
                chooseCategoryDialog.dismiss()
            }
            view.addCategory.setOnClickListener {
                val createNewCategoryDialog = AlertDialog.Builder(this).create()
                val createView = layoutInflater.inflate(R.layout.new_category_dialog_layout, null)
                createNewCategoryDialog.setView(createView)
                createView.cancelButton.setOnClickListener { createNewCategoryDialog.dismiss() }
                createView.okButton.setOnClickListener {
                    Log.d("MyTest", createView.enterCategory.text.toString())
                    sharedPreference.updateCategoriesList(createView.enterCategory.text.toString())
                    category = createView.enterCategory.text.toString()
                    toolBarTitle.text = category
                    createNewCategoryDialog.dismiss()
                }
                createNewCategoryDialog.show()
                chooseCategoryDialog.dismiss()
            }
            chooseCategoryDialog.show()
        }
    }

    private fun setUpAppBar() {
        setSupportActionBar(noteToolBar)
        noteToolBar.setNavigationIcon(R.drawable.back)
        noteToolBar.setNavigationOnClickListener { finish() }
    }

    private fun setUpTextViews() {
        when (intent.getStringExtra(Constants.CLICK_IDENTIFIER)) {
            Constants.LIST_VIEW_CLICK -> {
                id = intent.getIntExtra(Constants.NOTE_ID, -1)
                newNote = false
                val realmInstance = Realm.getDefaultInstance()
                val note = RealmMethods.getNoteWithId(id, realmInstance)
                toolBarTitle.text = note?.category
                noteTitle.setText(note?.title)
                noteContent.setText(note?.content)
                priority = note?.priority
                realmInstance.close()
            }
            Constants.FAB_BUTTON -> {
                newNoteFormalities()
            }
            Constants.SPEECH_TO_TEXT_FAB_CLICKED -> {
                newNoteFormalities()
                noteContent.setText(intent.getStringExtra(Constants.NOTE_CONTENT), TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun setToolBarText() {
        val realmInstance = Realm.getDefaultInstance()
        val note = RealmMethods.getNoteWithId(id, realmInstance)
        if (!newNote) { category = note?.category.toString() }
        toolBarTitle.text = category
    }

    private fun newNoteFormalities() {
        newNote = true
        id = sharedPreference.getLatestId()
    }

    private val saveNoteButtonListener = View.OnClickListener {
        val dateTime = Calendar.getInstance().time
        val dateTimeString = dateTime.toString("dd/MM/yyyy HH:mm:ss")

        if (newNote) {
            val note = NoteModel()
            note.apply {
                id = this@NoteActivity.id
                title = noteTitle.text.toString()
                content = noteContent.text.toString()
                category = this@NoteActivity.category
                priority = this@NoteActivity.priority!!
                starred = isNoteStarred
                createdDateTime = dateTimeString
                modifiedDateTime = dateTimeString
            }
            RealmMethods.saveNote(note)
            finish()
        } else {
            RealmMethods.updateNoteWithId(
                id,
                priority!!,
                toolBarTitle.text.toString(),
                noteTitle.text.toString(),
                noteContent.text.toString(),
                dateTimeString,
                isNoteStarred
            )
            finish()
        }
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.notes_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.title) {
            getString(R.string.details) -> {
                Log.d("MyTest", "Details")
                val dialogBuilder = AlertDialog.Builder(this).create()
                if (newNote) {
                    Log.d("MyTest", "no note dialog")
                    val view = layoutInflater.inflate(R.layout.no_note_details_dialog_layout, null)
                    dialogBuilder.apply {
                        setTitle("Alert message")
                        setView(view)
                    }
                    view.message.text = alertMessage
                    view.dismissAlert.setOnClickListener {
                        dialogBuilder.dismiss()
                    }
                } else {
                    Log.d("MyTest", "no note dialog")
                    val view = layoutInflater.inflate(R.layout.note_details_dialog_layout, null)
                    dialogBuilder.apply {
                        setView(view)
                    }
                    val realmInstance = Realm.getDefaultInstance()
                    val note = RealmMethods.getNoteWithId(id, realmInstance)
                    view.modifiedTimeValue.text = note?.modifiedDateTime
                    view.createdTimeValue.text = note?.createdDateTime
                    realmInstance.close()
                    view.dismissDetails.setOnClickListener {
                        dialogBuilder.dismiss()
                    }
                }
                dialogBuilder.show()
                true
            }
            getString(R.string.send) -> {
                val shareNoteIntent = Intent(Intent.ACTION_SEND)
                shareNoteIntent.type = "text/plain"
                shareNoteIntent.putExtra(Intent.EXTRA_TEXT, noteContent.text.toString())
                shareNoteIntent.putExtra(Intent.EXTRA_TITLE, noteTitle.text.toString())
                if (newNote) { Toast.makeText(this, "Save the note to share.", Toast.LENGTH_SHORT).show() }
                else { startActivity(shareNoteIntent) }
                true
            }
            getString(R.string.delete) -> {
                if (newNote) {
                    Toast.makeText(this, "Note not yet created.", Toast.LENGTH_SHORT).show()
                }
                else {
                    RealmMethods.deleteNoteGivenId(id)
                    Toast.makeText(this, "Note successfully deleted.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                true
            }
            getString(R.string.starred) -> {
                if (!isNoteStarred) {
                    item.setIcon(R.drawable.white_star)
                    isNoteStarred = true
                } else {
                    item.setIcon(R.drawable.star_outline)
                    isNoteStarred = false
                }
                true
            }
            getString(R.string.set_importance) -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun categoryChoose(category: String) {
        this.category = category
        toolBarTitle.text = this.category
    }

    override fun categoryDeleted() {
        this.category = Constants.DEFAULT_CATEGORY
        toolBarTitle.text = this.category
    }

}