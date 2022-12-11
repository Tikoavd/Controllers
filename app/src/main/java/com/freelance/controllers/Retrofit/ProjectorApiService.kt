package com.freelance.controllers.Retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ProjectorApiService {
    @POST
    fun projectorOn(@Query("~XX00 1 ~Password1") name: String = ""): Call<String>

    @POST
    fun projectorOff(@Query("~XX00 1 ~Password1") name: String = ""): Call<String>
}