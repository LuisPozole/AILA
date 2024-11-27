package com.example.aila

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoteDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        val title = intent.getStringExtra("noteTitle")
        val content = intent.getStringExtra("noteContent")

        findViewById<TextView>(R.id.tvNoteTitle).text = title
        findViewById<TextView>(R.id.tvNoteContent).text = content
    }
}
