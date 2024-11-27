package com.example.aila

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

data class Note(val title: String, val content: String)

class NotesActivity : AppCompatActivity() {

    private lateinit var etNoteTitle: EditText
    private lateinit var etNoteInput: EditText
    private lateinit var lvNotes: ListView
    private lateinit var notesAdapter: ArrayAdapter<String>
    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        etNoteTitle = findViewById(R.id.etNoteTitle)
        etNoteInput = findViewById(R.id.etNoteInput)
        lvNotes = findViewById(R.id.lvNotes)
        val btnRecordNote = findViewById<Button>(R.id.btnRecordNote)
        val btnSaveNote = findViewById<Button>(R.id.btnSaveNote)
        val btnDeleteAllNotes = findViewById<Button>(R.id.btnDeleteAllNotes)

        // Configurar ListView
        notesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList.map { it.title })
        lvNotes.adapter = notesAdapter

        // Guardar nota
        btnSaveNote.setOnClickListener {
            val title = etNoteTitle.text.toString()
            val content = etNoteInput.text.toString()
            if (title.isNotEmpty() && content.isNotEmpty()) {
                notesList.add(Note(title, content))
                updateListView()
                saveNotes()
                etNoteTitle.text.clear()
                etNoteInput.text.clear()
            } else {
                Toast.makeText(this, "Escribe un título y contenido para la nota", Toast.LENGTH_SHORT).show()
            }
        }

        // Iniciar dictado de voz
        btnRecordNote.setOnClickListener {
            startVoiceRecognition()
        }

        // Eliminar todas las notas
        btnDeleteAllNotes.setOnClickListener {
            notesList.clear()
            updateListView()
            saveNotes()
        }

        // Abrir una nota al tocarla
        lvNotes.setOnItemClickListener { _, _, position, _ ->
            val selectedNote = notesList[position]
            val intent = Intent(this, NoteDetailActivity::class.java).apply {
                putExtra("noteTitle", selectedNote.title)
                putExtra("noteContent", selectedNote.content)
            }
            startActivity(intent)
        }

        // Cargar notas almacenadas
        loadNotes()
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        try {
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            Toast.makeText(this, "Tu dispositivo no soporta reconocimiento de voz", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            etNoteInput.setText(results?.get(0) ?: "")
        }
    }

    private fun saveNotes() {
        val sharedPrefs = getSharedPreferences("NotesPrefs", MODE_PRIVATE)
        val notesJson = Gson().toJson(notesList)
        sharedPrefs.edit().putString("notes", notesJson).apply()
    }

    private fun loadNotes() {
        val sharedPrefs = getSharedPreferences("NotesPrefs", MODE_PRIVATE)
        val notesJson = sharedPrefs.getString("notes", "[]")
        val type = object : TypeToken<List<Note>>() {}.type
        notesList.clear()
        notesList.addAll(Gson().fromJson(notesJson, type))
        updateListView()
    }

    private fun updateListView() {
        notesAdapter.clear()
        notesAdapter.addAll(notesList.map { it.title })
        notesAdapter.notifyDataSetChanged()
    }
}
