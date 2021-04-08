package com.roshan.mynotes.ui

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.roshan.mynotes.ui.ChecklistListFragment
import com.roshan.mynotes.ui.NotesListFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments : List<Fragment> = listOf(
        NotesListFragment(),
        ChecklistListFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("MyTest", "On Created")
        return fragments[position]
    }
}