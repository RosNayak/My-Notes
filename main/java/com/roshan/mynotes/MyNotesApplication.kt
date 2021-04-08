package com.roshan.mynotes

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyNotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(applicationContext)

        val realmConfig = RealmConfiguration.Builder()
            .name("mynotes.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(realmConfig)
    }
}