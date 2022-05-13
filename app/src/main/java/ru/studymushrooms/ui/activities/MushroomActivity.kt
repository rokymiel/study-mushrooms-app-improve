package ru.studymushrooms.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ru.studymushrooms.R
import ru.studymushrooms.api.MushroomModel

private const val MODEL_EXTRAS_KEY = "model"

class MushroomActivity : AppCompatActivity() {

    private lateinit var model: MushroomModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mushroom)
        model = intent.getSerializableExtra(MODEL_EXTRAS_KEY) as MushroomModel
        val title: TextView = findViewById(R.id.mushroom_title)
        val image: ImageView = findViewById(R.id.mushroom_image)
        val content: TextView = findViewById(R.id.mushroom_content)
        title.text = model.name
        Picasso.get().load(model.pictureLink).into(image)
        content.text = model.description
    }

    companion object {
        fun newInstance(context: Context, mushroomModel: MushroomModel): Intent {
            return Intent(context, MushroomActivity::class.java).apply {
                putExtra(MODEL_EXTRAS_KEY, mushroomModel)
            }
        }
    }
}
