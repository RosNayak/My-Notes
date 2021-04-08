package com.roshan.mynotes.ui

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roshan.mynotes.R
import com.roshan.mynotes.callbacks.OnCategoryChooseListener
import com.roshan.mynotes.handler.PreferenceHandler
import kotlinx.android.synthetic.main.category_view.view.*

class CategoriesRecyclerView(
    private val context: Context,
    private val categories : ArrayList<String>,
    private val listener : OnCategoryChooseListener
) : RecyclerView.Adapter<CategoriesRecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.categoryName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.category_view, parent, false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("MyTest", "Called Bind")
        holder.textView.text = categories[position]
        holder.textView.setOnClickListener {
            listener.categoryChoose(it.categoryName.text.toString())
        }
        holder.textView.setOnLongClickListener {
            val longClickDialogBox = AlertDialog.Builder(context)
            longClickDialogBox.setTitle("Delete category")
            longClickDialogBox.setMessage("Would you like to delete this category?")
            longClickDialogBox.setPositiveButton("Yes") { _, _ ->
                val sharedPreferences = PreferenceHandler(context)
                sharedPreferences.removeCategoryFromList(categories[position])
                categories.remove(categories[position])
                longClickDialogBox.create().dismiss()
                listener.categoryDeleted()
                notifyDataSetChanged()
            }
            longClickDialogBox.setNegativeButton("No") { _, _ ->
                longClickDialogBox.create().dismiss()
            }
            longClickDialogBox.create().show()
            true
        }
    }
}