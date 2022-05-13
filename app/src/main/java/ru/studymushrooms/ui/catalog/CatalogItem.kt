package ru.studymushrooms.ui.catalog

import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ru.studymushrooms.R
import ru.studymushrooms.api.MushroomModel
import ru.studymushrooms.ui.activities.MushroomActivity

// TODO: change to mapping in repository
private const val MAX_DESCRIPTION_LENGTH = 30
const val IMAGE_PREFIX = "/image"
const val IMAGE_BASE_URL = "https://wikigrib.ru"

class CatalogItem(val mushroom: MushroomModel) : Item() {
    private val typeToKey = mapOf(
        "edible" to R.string.edible_mushroom,
        "halfedible" to R.string.halfedible_mushroom,
        "inedible" to R.string.inedible_mushroom
    )

    override fun getLayout(): Int = R.layout.catalog_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val context = viewHolder.itemView.context

        val titleEditText = viewHolder.itemView.findViewById<TextView>(R.id.card_title)
        titleEditText.text = mushroom.name

        val primaryEditText = viewHolder.itemView.findViewById<TextView>(R.id.card_primary)
        primaryEditText.text = typeToKey[mushroom.type]?.let { context.getString(it) }

        val secondaryEditText = viewHolder.itemView.findViewById<TextView>(R.id.card_secondary)
        if (mushroom.description.length > MAX_DESCRIPTION_LENGTH) {
            secondaryEditText.text = context.getString(
                R.string.ellipsised_description_template,
                mushroom.description.substring(0..MAX_DESCRIPTION_LENGTH)
            )
        } else {
            secondaryEditText.text = mushroom.description
        }

        if (mushroom.pictureLink.startsWith(IMAGE_PREFIX)) {
            mushroom.pictureLink = IMAGE_BASE_URL + mushroom.pictureLink
        }

        val image = viewHolder.itemView.findViewById<ImageView>(R.id.card_image)
        Picasso.get().load(mushroom.pictureLink).into(image)

        val card = viewHolder.itemView.findViewById<MaterialCardView>(R.id.card)
        card.setOnClickListener {
            val intent = MushroomActivity.newInstance(context, mushroom)
            context.startActivity(intent)
        }
    }
}