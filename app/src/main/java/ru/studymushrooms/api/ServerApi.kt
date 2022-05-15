package ru.studymushrooms.api

import com.cocoahero.android.geojson.Point
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.Serializable
import java.util.*

interface ServerApi {

    @POST("/api/register")
    suspend fun register(@Body loginModel: LoginModel): TokenResponse

    @GET("/api/user_info")
    suspend fun getUserInfo(@Header("Authorization") token: String): UserModel

    @POST("/api/login")
    suspend fun login(@Body loginModel: LoginModel): TokenResponse

    @POST("/api/recognize")
    suspend fun recognize(
        @Header("Authorization") token: String,
        @Body body: ImageRequest
    ): List<RecognitionModel>

    @GET("/api/notes")
    suspend fun getNotes(@Header("Authorization") token: String): List<NoteModel>

    @POST("/api/notes")
    suspend fun postNote(
        @Header("Authorization") token: String,
        @Body noteModel: NoteModel
    ): ResponseBody

    @PUT("/api/notes/update") // todo протестить
    suspend fun updateNote(
        @Header("Authorization") token: String,
        @Body noteModel: NoteModel
    ): ResponseBody

    @DELETE("/api/notes/delete") // todo протестить
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Body noteModel: NoteModel
    ): ResponseBody

    @GET("/api/places")
    suspend fun getPlaces(@Header("Authorization") token: String): List<PlaceModel>

    @POST("/api/places")
    suspend fun addPlace(
        @Header("Authorization") token: String,
        @Body place: PlaceModel
    ): ResponseBody

    @GET("/api/mushrooms")
    suspend fun getMushrooms(
        @Header("Authorization") token: String, @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): List<MushroomModel>
}

data class LoginModel(
    @Expose @SerializedName("email") var email: String?,
    @Expose @SerializedName("username") var username: String,
    @Expose @SerializedName("password") var password: String
)

data class TokenResponse(@Expose @SerializedName("token") var token: String)

data class MushroomModel(
    @Expose @SerializedName("pk") var id: Int,
    @Expose @SerializedName("name") var name: String,
    @Expose @SerializedName("type") var type: String,
    @Expose @SerializedName("picture_link") var pictureLink: String,
    @Expose @SerializedName("description") var description: String
) : Serializable

data class PlaceModel(
    @Expose @SerializedName("location") var location: Point?,
    @Expose @SerializedName("longitude") var longitude: Double?,
    @Expose @SerializedName("latitude") var latitude: Double?,
    @Expose @SerializedName("image") var rawImage: String,
    @Expose @SerializedName("date") var date: Date?,
    @Expose @SerializedName("use_location") var useLocation: Boolean
)

data class NoteModel(
    @Expose @SerializedName("pk") var id: Int?,
    @Expose @SerializedName("title") var title: String?,
    @Expose @SerializedName("date") var date: Date,
    @Expose @SerializedName("user") var author: Int?,
    @Expose @SerializedName("content") var content: String?
)

data class UserModel(
    @Expose @SerializedName("username") var username: String,
    @Expose @SerializedName("email") var email: String,
    @Expose @SerializedName("pk") var id: Int,
    @Expose @SerializedName("mushroom_places") var places: List<PlaceModel>,
    @Expose @SerializedName("notes") var notes: List<NoteModel>
)

data class ImageRequest(@Expose @SerializedName("image") var rawImage: String)

data class RecognitionModel(
    @Expose @SerializedName("probability") var prob: Double,
    @Expose @SerializedName("mushroom") var mushroom: MushroomModel
)