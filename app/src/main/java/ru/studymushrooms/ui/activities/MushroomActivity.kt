package ru.studymushrooms.ui.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ru.studymushrooms.R
import ru.studymushrooms.api.MushroomModel

class MushroomActivity : AppCompatActivity() {

    private lateinit var model: MushroomModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mushroom)
        model = intent.getSerializableExtra("model") as MushroomModel
        val title: TextView = findViewById(R.id.mushroom_title)
        val image: ImageView = findViewById(R.id.mushroom_image)
        val content: TextView = findViewById(R.id.mushroom_content)
        title.text = model.name
        Picasso.get().load(model.pictureLink).into(image)
        content.text = model.description
    }
}
