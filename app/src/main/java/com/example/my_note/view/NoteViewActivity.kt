package com.example.my_note.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.my_note.databinding.ActivityNoteViewBinding
import com.example.my_note.model.Dao
import com.example.my_note.model.NoteData
import com.example.my_note.model.NoteDataBase
import com.example.my_note.util.DetailForUpdate
import com.example.my_note.util.TitleForUpdate
import com.example.my_note.util.idForUpdate
import com.example.my_note.util.positionForUpdate
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NoteViewActivityTest"


class NoteViewActivity : AppCompatActivity() {


    lateinit var binding: ActivityNoteViewBinding
    lateinit var title: String
    lateinit var detail: String
    lateinit var noteDao: Dao
    lateinit var allNote: List<NoteData>

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data

                title = data?.getStringExtra(TitleContent).toString()
                detail = data?.getStringExtra(DetailContent).toString()

                val c: Date = Calendar.getInstance().time

                val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val dateFormat: String = df.format(c)

                val tf = SimpleDateFormat(" HH:mm", Locale.getDefault())
                val timeFormat = tf.format(c)

                val newItem = NoteData(
                    id = intent.getIntExtra(idForUpdate, 0),
                    title = title,
                    details = detail,
                    date = dateFormat,
                    time = timeFormat,
                )

                noteDao.insertOrUpdate(newItem)


            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatabase()
        handleEvents()
    }

    override fun onResume() {
        super.onResume()

        binding.txtTitle.text = title
        binding.txtDetail.text = detail

    }

    private fun setupDatabase() {

        noteDao = NoteDataBase.getDatabase(this).noteDao

        allNote = noteDao.getAllNote().reversed()
        val id = intent.getIntExtra(positionForUpdate, 0)

        title = allNote[id].title
        detail = allNote[id].details

    }

    private fun handleEvents() {

        binding.backBtnView.setOnClickListener {
            finish()
        }


        binding.edtBtn.setOnClickListener {

            openEditorActivityForUpdateResult(title, detail)

        }

    }

    private fun openEditorActivityForUpdateResult(title: String, detail: String) {

        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(TitleForUpdate, title)
        intent.putExtra(DetailForUpdate, detail)
        resultLauncher.launch(intent)

    }


}

