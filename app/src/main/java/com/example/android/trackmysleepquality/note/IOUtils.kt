package com.example.android.trackmysleepquality.note

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.FragmentActivity

fun showDialog(context: Context?, listener: (String) -> Unit) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Title")

    val input = EditText(context)
    input.hint = "Enter Text"
    input.inputType = InputType.TYPE_CLASS_TEXT
    builder.setView(input)

    builder.setPositiveButton("OK") { _, _ -> listener.invoke(input.text.toString()) }
    builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

    builder.show()
}