package com.roshan.mynotes.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.children
import com.google.android.material.tabs.TabLayoutMediator
import com.roshan.mynotes.Constants
import com.roshan.mynotes.R
import com.roshan.mynotes.handler.PreferenceHandler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val tabTitles : List<String> = listOf("Notes", "Checklist")
    private var isOptionsOpen : Boolean = false
    private lateinit var sharedPreferences: PreferenceHandler
    private var menu : Menu? = null
    private val CATEGORY_KEY : String = "Category"
    private val CATEGORIES_LIST_INDEX : Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uiSetUp()
        sharedPreferences = PreferenceHandler(this)
        menu = navView.menu.getItem(CATEGORIES_LIST_INDEX).subMenu
    }

    override fun onStart() {
        super.onStart()
        updateMenu()
    }

    private fun uiSetUp() {
        setUpViewPagerUi()
        setUpToolBar()
        setFabButtons()
    }

    private fun setUpViewPagerUi() {
        val adapter = ViewPagerAdapter(this)
        vpContent.adapter = adapter
        TabLayoutMediator(tabLayout, vpContent) { tab, position ->
            Log.d("MyTest", "Mediator")
            tab.text = tabTitles[position]
            vpContent.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun setFabButtons() {
        fabAnimations()
        fabClickListeners()
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.drawer)
        toolBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun fabAnimations() {
        speak.visibility = View.GONE
        textNote.visibility = View.GONE
        checklist.visibility = View.GONE

        options.setOnClickListener {
            if (!isOptionsOpen) {
                showOut()
            } else {
                showIn()
            }
        }
    }

    private fun showOut() {
        speak.visibility = View.VISIBLE
        textNote.visibility = View.VISIBLE
        checklist.visibility = View.VISIBLE

        val animatorSpeak = ObjectAnimator.ofFloat(speak, View.TRANSLATION_Y, -options.height.toFloat())
        val animatorTextNote = ObjectAnimator.ofFloat(textNote, View.TRANSLATION_Y, -options.height.toFloat())
        val animatorCheckList = ObjectAnimator.ofFloat(checklist, View.TRANSLATION_Y, -options.height.toFloat())
        val animatorOptions = ObjectAnimator.ofFloat(options, View.ROTATION, -135f)

        animatorSpeak.duration = 100
        animatorCheckList.duration = 100
        animatorOptions.duration = 100
        animatorTextNote.duration = 100

        animatorSpeak.start()
        animatorTextNote.start()
        animatorCheckList.start()
        animatorOptions.start()

        isOptionsOpen = true
    }

    private fun showIn() {
        val animatorSpeak = ObjectAnimator.ofFloat(speak, View.TRANSLATION_Y, 0f)
        val animatorTextNote = ObjectAnimator.ofFloat(textNote, View.TRANSLATION_Y, 0f)
        val animatorCheckList = ObjectAnimator.ofFloat(checklist, View.TRANSLATION_Y, 0f)
        val animatorOptions = ObjectAnimator.ofFloat(options, View.ROTATION, 0f)

        animatorSpeak.duration = 100
        animatorCheckList.duration = 100
        animatorOptions.duration = 100
        animatorTextNote.duration = 100

        animatorSpeak.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                speak.visibility = View.GONE
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}

        })
        animatorTextNote.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                textNote.visibility = View.GONE
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}

        })
        animatorCheckList.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                checklist.visibility = View.GONE
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}

        })

        animatorSpeak.start()
        animatorTextNote.start()
        animatorCheckList.start()
        animatorOptions.start()

        isOptionsOpen = false
    }

    private fun fabClickListeners() {
        textNote.setOnClickListener {
            val callNoteActivityIntent = Intent(this, NoteActivity::class.java)
            callNoteActivityIntent.putExtra(Constants.CLICK_IDENTIFIER, Constants.FAB_BUTTON)
            showIn()
            startActivity(callNoteActivityIntent)
        }

        speak.setOnClickListener {
            val startRecordAudioIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            startRecordAudioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            startRecordAudioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            startActivityForResult(startRecordAudioIntent, Constants.SPEECH_TO_TEXT_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.SPEECH_TO_TEXT_RESULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("MyTest", "${data == null}")
                    try {
                        val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        Log.d("MyTest", "1")
                        val startNoteActivityIntent = Intent(this, NoteActivity::class.java)
                        startNoteActivityIntent.putExtra(Constants.CLICK_IDENTIFIER, Constants.SPEECH_TO_TEXT_FAB_CLICKED)
                        startNoteActivityIntent.putExtra(Constants.NOTE_CONTENT, result?.get(0))
                        Log.d("MyTest", "2")
                        startActivity(startNoteActivityIntent)
                    } catch (error : NullPointerException) {
                        Toast.makeText(this, "Could not convert speech to text.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateMenu() {
        if (menu != null) {
            val categories = sharedPreferences.getCategoriesList()
            if (categories != null) {
                val items = menu!!.children.iterator()
                val menuItemsName = ArrayList<String>()
                items.forEach { menuItemsName.add(it.title.toString()) }
                for (category in categories) {
                    if (!menuItemsName.contains(category)) {
                        val menuItem = menu!!.add(category)
                        menuItem.setOnMenuItemClickListener {
                            val startCategoryActivityIntent = Intent(this@MainActivity , CategoryActivity::class.java)
                            startCategoryActivityIntent.putExtra(CATEGORY_KEY, category)
                            startActivity(startCategoryActivityIntent)
                            true
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                val callSearchNoteActivityIntent = Intent(this, SearchNoteActivity::class.java)
                startActivity(callSearchNoteActivityIntent)
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

}