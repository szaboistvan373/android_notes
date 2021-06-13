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

package com.example.android.trackmysleepquality.note

import android.content.Context
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.Note
import com.example.android.trackmysleepquality.database.NoteDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//class NotesViewModel(private val context: Context, private val database: NoteDatabaseDao): ViewModel() {
//
//    val notes: MutableLiveData<List<Note>> = MutableLiveData<List<Note>>(listOf<Note>(Note(1, "asd"), Note(2, "asdasda")))
//
//    fun onAdd() {
//
//        showDialog(context) { a -> println(a) }
////        database.insert(Note(text = "asd"))
//    }

class NotesViewModel(
    private val noteKey: Long?,
    private val context: Context,
    private val database: NoteDatabaseDao
) :
    ViewModel() {

    val dbNotes = database.getNotesWithParentIdLazily(noteKey)

    var note: Note? = null

    init {
        viewModelScope.launch {
            asd()
        }
    }

    private suspend fun asd() {
        withContext(Dispatchers.IO) {
            note = if (noteKey != null) database.getNoteWithId(noteKey) else null
        }
    }

    private val _navigateToNote = MutableLiveData<Long>()
    val navigateToNote
        get() = _navigateToNote

    fun onNoteClicked(id: Long) {
        _navigateToNote.value = id
    }

    fun onNoteNavigated() {
        _navigateToNote.value = null
    }

    fun onAdd() {
        showDialog(context) { a ->
            viewModelScope.launch {
                add(a)
            }
        }
    }

    fun onNoteClickedOld(id: Long) {
        viewModelScope.launch {
            asdasdasd(id)
        }
    }

    private suspend fun add(value: String) {
        withContext(Dispatchers.IO) {
            database.insert(Note(text = value, parentId = noteKey))
        }
    }

    private suspend fun asdasdasd(id: Long) {
        withContext(Dispatchers.IO) {
            val note = database.getNoteWithId(id) ?: return@withContext
            note.notes += Note(text = "child cucc", parentId = note.id)
            database.updateNoteWithNotes(note)
            val noteWithNotes = database.getNoteWithNotes(note.id)
            println("asd")
//            database.update()
//            database.update(noteWithChildWithId)
        }
    }

//    private suspend fun noteClicked(id: Long) {
//        withContext(Dispatchers.IO) {
//        }
//    }
}

 