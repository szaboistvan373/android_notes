package com.example.android.keeptrack.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDatabaseDao {

    @Insert(onConflict = IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("DELETE FROM note_table WHERE id = :key")
    suspend fun delete(key: Long)

    @Query("SELECT * FROM note_table")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * from note_table WHERE id = :key")
    fun getNoteWithId(key: Long): LiveData<Note>

    @Query("SELECT * from note_table WHERE id = :key")
    fun getNoteWithIdEagerly(key: Long): Note?

    @Query("SELECT * from note_table WHERE parentId = :parentId or (:parentId is null and parentId is null)")
    fun getNotesWithParentId(parentId: Long?): LiveData<List<Note>>

    @Query("SELECT * from note_table WHERE parentId = :parentId or (:parentId is null and parentId is null)")
    fun getNotesWithParentIdEagerly(parentId: Long?): List<Note>
}

