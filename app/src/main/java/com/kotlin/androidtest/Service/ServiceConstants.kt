package com.kotlin.androidtest.Service

class ServiceConstants {
    val BASE_URL = "https://api.github.com/"

    companion object {
        //api end points
        const val USERS = "users"
        const val USERSPROFILE = "users/{username}"

        //query name
        const val SINCE = "since"
        const val USERNAME = "username"
    }



}