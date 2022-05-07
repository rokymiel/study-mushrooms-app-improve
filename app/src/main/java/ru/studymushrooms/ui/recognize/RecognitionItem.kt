package ru.studymushrooms.ui.recognize

import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ru.studymushrooms.R
import ru.studymushrooms.api.RecognitionModel
import ru.studymushrooms.ui.activities.MushroomActivity
import ru.studymushrooms.ui.catalog.IMAGE_BASE_URL
import ru.studymushrooms.ui.catalog.IMAGE_PREFIX

class RecognitionItem(val recognition: RecognitionModel) : Item<GroupieViewHolder>() {
    private val typeToKey = mapOf(
        "edible" to R.string.edible_mushroom,
        "halfedible" to R.string.halfedible_mushroom,
        "inedible" to R.string.inedible_mushroom
    )

    override fun getLayout(): Int = R.layout.catalog_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val context = viewHolder.itemView.context

        val titleEditText = viewHolder.itemView.findViewById<TextView>(R.id.card_title)
        titleEditText.text = recognition.mushroom.name

        val primaryEditText = viewHolder.itemView.findViewById<TextView>(R.id.card_primary)
        primaryEditText.text = typeToKey[recognition.mushroom.type]?.let { context.getString(it) }

        val secondaryEditText = viewHolder.itemView.findViewById<TextView>(R.id.card_secondary)
        val secondaryText =  context.getString(R.string.proba_template, recognition.prob * 100)
        secondaryEditText.text = secondaryText

        if (recognition.mushroom.pictureLink.startsWith(IMAGE_PREFIX)) {
            recognition.mushroom.pictureLink =
                IMAGE_BASE_URL + recognition.mushroom.pictureLink
        }
        val image = viewHolder.itemView.findViewById<ImageView>(R.id.card_image)
        Picasso.get().load(recognition.mushroom.pictureLink).into(image)

        val card = viewHolder.itemView.findViewById<MaterialCardView>(R.id.card)
        card.setOnClickListener {
            val intent = MushroomActivity.newInstance(context, recognition.mushroom)
            context.startActivity(intent)
        }
    }
}