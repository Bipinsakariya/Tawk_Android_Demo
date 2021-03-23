package com.kotlin.androidtest.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(userEntity: UserEntity)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserProfile(profileEntity: ProfileEntity)

    @Query("UPDATE profile_table SET notes = :note WHERE login = :login")
    fun updateUserProfile(note: String, login: String)

    @Query("UPDATE user_table SET notes = :note WHERE login = :login")
    fun updateUsernotes(note: String, login: String)

    @Query("SELECT * from profile_table WHERE  login= :login")
    fun getProfileFromDB(login: String): ProfileEntity

}