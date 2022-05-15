package ru.studymushrooms.data.mappers

import ru.studymushrooms.api.MushroomModel

private const val IMAGE_PREFIX = "/image"
private const val IMAGE_BASE_URL = "https://wikigrib.ru"

class MushroomMapper {
    fun mapMushroom(mushroom: MushroomModel): MushroomModel {
        val pictureLink = if (mushroom.pictureLink.startsWith(IMAGE_PREFIX)) {
            IMAGE_BASE_URL + mushroom.pictureLink
        } else {
            mushroom.pictureLink
        }
        return mushroom.copy(pictureLink = pictureLink)
    }
}