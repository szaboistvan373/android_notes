/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE

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

    @Query("SELECT * from note_table WHERE parentId = :parentId or (:parentId is null and parentId is null)")
    abstract fun getNotesWithParentId(parentId: Long?): List<Note>

    @Query("SELECT * from note_table WHERE parentId = :parentId or (:parentId is null and parentId is null)")
    abstract fun getNotesWithParentIdLazily(parentId: Long?): LiveData<List<Note>>
//
//    @Transaction
//    @Query("SELECT * from note_table WHERE id = :key LIMIT 1")
//    abstract suspend fun getNoteWithChildWithId(key: Long): NoteWithNotes?

    suspend fun updateNoteWithNotes(note: Note) {
        val notes = note.notes
        for (i in notes.indices) {
            notes[i].parentId = note.id
            insert(notes[i])
        }
    }

    suspend fun getNoteWithNotes(key: Long): Note? {
        // TODO: multi level
        val note = getNoteWithId(key) ?: return null
        note.notes = getNotesWithParentId(key)
        return note
    }

//    fun getUserWithPets(id: Int): User? {
//        val user: User = getUser(id)
//        val pets: List<Pet> = getPetList(id)
//        user.setPetList(pets)
//        return user
//    }

//    @Transaction
//    @Query("SELECT * FROM note_table")
//    fun getNotesWithChildNotes(): List<NoteWithNotes>
}

