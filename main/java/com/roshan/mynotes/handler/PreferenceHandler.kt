package com.roshan.mynotes.handler

import android.content.Context
import com.google.gson.Gson
import com.roshan.mynotes.Constants

class PreferenceHandler(context: Context) {

    companion object {
        const val ID_KEY: String = "Latest Id"
        const val CATEGORIES_KEY : String = "Categories"
    }

    private val sharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLatestId(): Int {
        val latestId: Int = sharedPreferences.getInt(ID_KEY, 0)
        val editor = sharedPreferences.edit()
        editor.putInt(ID_KEY, latestId + 1).apply()
        return latestId
    }

    fun updateCategoriesList(newCategory : String) {
        val gson = Gson()
        val categories = sharedPreferences.getString(CATEGORIES_KEY, null)
        var categoriesList = gson.fromJson<ArrayList<String>>(categories, ArrayList::class.java)
        categoriesList = categoriesList ?: ArrayList()
        categoriesList.add(newCategory)
        val updatedCategories = gson.toJson(categoriesList)
        sharedPreferences.edit().putString(CATEGORIES_KEY, updatedCategories).apply()
    }

    fun removeCategoryFromList(category : String) {
        val gson = Gson()
        val categories = sharedPreferences.getString(CATEGORIES_KEY, null)
        val categoriesList = gson.fromJson<ArrayList<String>>(categories, ArrayList::class.java)
        categoriesList.remove(category)
        val updatedCategories = gson.toJson(categoriesList)
        sharedPreferences.edit().putString(CATEGORIES_KEY, updatedCategories).apply()
    }

    fun getCategoriesList() : ArrayList<String>? {
        val gson = Gson()
        val categories = sharedPreferences.getString(CATEGORIES_KEY, null)
        return gson.fromJson<ArrayList<String>>(categories, ArrayList::class.java)
    }
}