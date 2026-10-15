package com.example.neuroocr

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var result: TextView

    private val picker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) recognize(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(32, 48, 32, 24)
        }

        layout.addView(TextView(this).apply {
            text = "Neuro OCR"
            textSize = 28f
        })

        layout.addView(Button(this).apply {
            text = "Выбрать изображение"
            setOnClickListener { picker.launch("image/*") }
        })

        status = TextView(this).apply {
            text = "Выбери картинку из галереи."
            textSize = 16f
        }
        result = TextView(this).apply {
            textSize = 18f
            setTextIsSelectable(true)
        }

        layout.addView(status)
        layout.addView(result)
        setContentView(layout)
    }

    private fun recognize(uri: Uri) {
        status.text = "Распознаю текст..."
        val recognizer = TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

        try {
            val image = InputImage.fromFilePath(this, uri)
            recognizer.process(image)
                .addOnSuccessListener { text ->
                    result.text = text.text.ifBlank {
                        "Текст не найден."
                    }
                    status.text = "Готово."
                    recognizer.close()
                }
                .addOnFailureListener { error ->
                    status.text = "Ошибка распознавания."
                    Toast.makeText(
                        this,
                        error.localizedMessage ?: "Не удалось распознать",
                        Toast.LENGTH_LONG
                    ).show()
                    recognizer.close()
                }
        } catch (error: Exception) {
            status.text = "Не удалось открыть изображение."
            recognizer.close()
        }
    }
}
