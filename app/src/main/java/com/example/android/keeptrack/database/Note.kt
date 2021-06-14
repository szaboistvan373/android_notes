package com.example.android.keeptrack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "text")
    var text: String,

    @ColumnInfo(name = "parentId")
    var parentId: Long?
)
