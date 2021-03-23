package com.kotlin.androidtest.Service

import com.kotlin.androidtest.Model.User
import com.kotlin.androidtest.Model.UserProfile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BackendService {

    @GET(ServiceConstants.USERS)
    fun getlist(@Query(ServiceConstants.SINCE) since: Int): Call<User>

    @GET(ServiceConstants.USERSPROFILE)
    fun getprofilelist(@Path(ServiceConstants.USERNAME) username: String): Call<UserProfile>
}