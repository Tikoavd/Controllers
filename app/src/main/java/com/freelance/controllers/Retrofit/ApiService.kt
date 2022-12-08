package com.freelance.controllers.Retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("/index.html")
    fun socketOn(@Field("btnpwr") command: String = "on"): Call<String>

    @FormUrlEncoded
    @POST("/index.html")
    fun socketOff(@Field("btnpwr") command: String = "off"): Call<String>
}