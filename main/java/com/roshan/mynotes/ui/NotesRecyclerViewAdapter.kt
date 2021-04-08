package com.roshan.mynotes.ui

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import com.roshan.mynotes.R
import com.roshan.mynotes.callbacks.OnNotesItemClickListener
import com.roshan.mynotes.localDb.RealmMethods
import com.roshan.mynotes.localDb.realmModels.NoteModel
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_note.view.*

class NotesRecyclerViewAdapter(
    private val activity: AppCompatActivity,
    data : OrderedRealmCollection<NoteModel>,
    private val notesItemClickListener: OnNotesItemClickListener,
    autoUpdate : Boolean
) : RealmRecyclerViewAdapter<NoteModel, NotesRecyclerViewAdapter.ViewHolder>(data, autoUpdate){

    private var isActionModeEnabled : Boolean = false
    private val selectedItems : ArrayList<Int> = ArrayList()
    private var actionMode : ActionMode? = null
    private var dataset = data

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val categoryView : TextView = itemView.noteCategory
        val titleView : TextView = itemView.noteTitle
        val contentView : TextView = itemView.noteContent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(activity)
        return ViewHolder(inflater.inflate(R.layout.layout_note, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteData = data?.get(position)
        holder.categoryView.text = noteData?.category
        holder.titleView.text = noteData?.title
        holder.contentView.text = noteData?.content

        if (noteData?.id in selectedItems) {
            holder.itemView.alpha = 0.5f
        } else {
            holder.itemView.alpha = 1f
        }

        holder.itemView.setOnClickListener {
            if (!isActionModeEnabled) {
                notesItemClickListener.onItemClick(noteData?.id!!)
            } else {
                val clickedItemId = noteData?.id as Int
                if (clickedItemId in selectedItems) {
                    selectedItems.remove(clickedItemId)
                    holder.itemView.alpha = 1f

                    if (selectedItems.size == 0) {
                        actionMode?.finish()
                    }
                } else {
                    selectedItems.add(clickedItemId)
                    holder.itemView.alpha = 0.5f
                }
                actionMode?.title = "selected " + selectedItems.size.toString()
            }
        }

        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(view : View?): Boolean {
                if (!isActionModeEnabled) {
                    isActionModeEnabled = true
                    actionMode = activity.startSupportActionMode(callback)
                }

                if (noteData?.id in selectedItems) {
                    selectedItems.remove(noteData?.id as Int)
                    holder.itemView.alpha = 1f

                    if (selectedItems.size == 0) {
                        actionMode?.finish()
                    }
                } else {
                    selectedItems.add(noteData?.id as Int)
                    holder.itemView.alpha = 0.5f
                }
                actionMode?.title  = "selected " + selectedItems.size.toString()

                return true
            }

        })
    }

    private val callback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.delete -> {

                }
                R.id.changeCategory -> {

                }
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val menuInflater = mode?.menuInflater
            menuInflater?.inflate(R.menu.contexual_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            isActionModeEnabled = false
            selectedItems.clear()
            notifyDataSetChanged() //to remove the fades of selected items. redraw.
        }

    }

    fun changeDatasetGivenQuery(query : String) {
        dataset = RealmMethods.getNotesGivenQuery(query)
        notifyDataSetChanged()
    }

}