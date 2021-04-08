package com.roshan.mynotes.localDb.realmModels

import com.roshan.mynotes.enums.NotePriority
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class NoteModel(
    @Required @PrimaryKey
    var id : Int? = null,
    @Required
    var title : String? = null,
    @Required
    var content : String? = null,
    @Required
    var category : String? = null,
    var priority : String = NotePriority.DEFAULT_PRIORITY.value,
    var starred : Boolean = false,
    var createdDateTime : String = "",
    var modifiedDateTime : String = ""
) : RealmObject()