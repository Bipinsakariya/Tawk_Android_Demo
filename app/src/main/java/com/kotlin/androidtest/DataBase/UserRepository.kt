package com.kotlin.androidtest.DataBase

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAllData : LiveData<List<UserEntity>> = userDao.readAllData()

    suspend fun addUser(userEntity: UserEntity){
        userDao.addUser(userEntity)
    }

    suspend fun addUserProfile(profileEntity: ProfileEntity){
        userDao.addUserProfile(profileEntity)
    }
}