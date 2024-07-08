package com.example.newstoryapp.Api



import com.example.newstoryapp.response.AddStoryrespon
import com.example.newstoryapp.response.GetStoryRespon
import com.example.newstoryapp.response.ResponRegister
import com.example.newstoryapp.response.Responenter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ServiceAPI {

    @FormUrlEncoded
    @POST("register")
    fun getRegist(
        @Field("name") name :String,
        @Field("email") email : String,
        @Field("password") password : String
    ): Call<ResponRegister>

    @FormUrlEncoded
    @POST("login")
    fun getLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Responenter>


    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : GetStoryRespon

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<AddStoryrespon>

    @GET("stories?location=1")
    suspend fun getLocation(
        @Header("Authorization") token: String
    ): GetStoryRespon
}