package com.example.android.keeptrack.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class NoteDatabaseDao {

    @Insert(onConflict = IGNORE)
    abstract suspend fun insert(note: Note)

    @Update
    abstract suspend fun update(note: Note)

    @Query("DELETE FROM note_table WHERE id = :key")
    abstract suspend fun delete(key: Long)

    @Query("SELECT * FROM note_table")
    abstract fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * from note_table WHERE id = :key")
    abstract fun getNoteWithId(key: Long): Note?

    @Query("SELECT * from note_table WHERE id = :key")
    abstract fun getNoteWithIdLazily(key: Long): LiveData<Note>

    @Query("SELECT * from note_table WHERE parentId = :parentId or (:parentId is null and parentId is null)")
    abstract fun getNotesWithParentId(parentId: Long?): List<Note>

    @Query("SELECT * from note_table WHERE parentId = :parentId or (:parentId is null and parentId is null)")
    abstract fun getNotesWithParentIdLazily(parentId: Long?): LiveData<List<Note>>

    suspend fun updateNoteWithNotes(note: Note) {
        val notes = note.notes
        for (i in notes.indices) {
            notes[i].parentId = note.id
            insert(notes[i])
        }
    }

    suspend fun getNoteWithNotes(key: Long): Note? {
        val note = getNoteWithId(key) ?: return null
        note.notes = getNotesWithParentId(key)
        return note
    }
}

