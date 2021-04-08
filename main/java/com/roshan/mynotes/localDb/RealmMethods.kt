package com.roshan.mynotes.localDb


import com.roshan.mynotes.localDb.realmModels.NoteModel
import io.realm.Realm
import io.realm.RealmResults

object RealmMethods {

    fun saveNote(realmObject: NoteModel) {
        val realmInstance = Realm.getDefaultInstance()
        realmInstance.executeTransaction {
            it.insertOrUpdate(realmObject)
        }
        realmInstance.close()
    }

    fun getNoteWithId(id : Int, realmInstance: Realm) : NoteModel? {
        return realmInstance.where(NoteModel::class.java).equalTo("id", id).findFirst()
    }

    fun updateNoteWithId(
        id : Int,
        priority : String,
        category : String,
        title : String,
        content : String,
        modifiedDateTime : String,
        isStarred : Boolean
    ) {
        val realmInstance = Realm.getDefaultInstance()
        realmInstance.executeTransaction {
            val note =  it.where(NoteModel::class.java).equalTo("id", id).findFirst()
            note?.apply {
                this.priority = priority
                this.category = category
                this.title = title
                this.content = content
                this.modifiedDateTime = modifiedDateTime
                this.starred = isStarred
            }
        }
        realmInstance.close()
    }

    fun deleteNoteGivenId(noteId : Int) {
        val realmInstance = Realm.getDefaultInstance()
        val note = realmInstance.where(NoteModel::class.java).equalTo("id", noteId).findFirst()
        realmInstance.executeTransaction{
            note?.deleteFromRealm()
        }
        realmInstance.close()
    }

    fun getNotesGivenCategory(category : String) : RealmResults<NoteModel> {
        val realmInstance = Realm.getDefaultInstance()
        val notes = realmInstance.where(NoteModel::class.java).equalTo("category", category).findAll()
        realmInstance.close()
        return notes
    }

    fun getNotesGivenQuery(query : String) : RealmResults<NoteModel> {
        val realmInstance = Realm.getDefaultInstance()
        val notes = realmInstance.where(NoteModel::class.java).contains("title", query).findAll()
        realmInstance.close()
        return notes
    }



}