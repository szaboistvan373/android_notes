package com.example.android.keeptrack.database

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithNotes(
    @Embedded
    val note: Note,

    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    var notes: List<Note>
)
