package com.example.my_note.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.my_note.databinding.ActivityEditorBinding

const val TitleContent = "titleContent"
const val DetailContent = "detailContent"

class EditorActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIfEdtTextShouldFill()
        handleEvents()


    }

    private fun checkIfEdtTextShouldFill() {

        if (intent.getStringExtra(TitleForUpdate) != null) {

            val oldTitle = intent.getStringExtra(TitleForUpdate)
            val oldDetail = intent.getStringExtra(DetailForUpdate)

            binding.edtTitle.setText(oldTitle)
            binding.edtDetail.setText(oldDetail)

        }

    }

    private fun handleEvents() {

        binding.saveBtn.setOnClickListener {

            saveNote()

        }

        binding.backBtn.setOnClickListener {

            setResult(Activity.RESULT_CANCELED, intent)
            finish()

        }

        binding.edtTitle.addTextChangedListener {

            binding.titleLengthTxt.text = (30 - binding.edtTitle.length()).toString()

        }

    }

    private fun checkForIntent() {

        if (intent.getStringExtra(TitleForUpdate) != null) {

            val intentForUpdate = Intent(this, NoteViewActivity::class.java)
            intentForUpdate.putExtra(TitleContent, binding.edtTitle.text.toString())
            intentForUpdate.putExtra(DetailContent, binding.edtDetail.text.toString())
            setResult(Activity.RESULT_OK, intentForUpdate)


        } else {

            val intentForInsert = Intent(this, MainActivity::class.java)
            intentForInsert.putExtra(TitleContent, binding.edtTitle.text.toString())
            intentForInsert.putExtra(DetailContent, binding.edtDetail.text.toString())
            setResult(Activity.RESULT_OK, intentForInsert)
        }

    }

    private fun saveNote() {
        if (binding.edtTitle.text.isNotEmpty() && binding.edtDetail.text.isNotEmpty()) {

            val mAlert = AlertDialog.Builder(this)
                .setTitle("Let")
                .setMessage("Save changes ?")
                .setPositiveButton(
                    "Save"
                ) { _, _ ->
                    checkForIntent()
                    finish()
                }
                .setNegativeButton("Discard", null)
                .create()
            mAlert.show()

        } else {

            Toast.makeText(
                this,
                "write your note completely to remember that !",
                Toast.LENGTH_SHORT
            ).show()

        }
    }


}

