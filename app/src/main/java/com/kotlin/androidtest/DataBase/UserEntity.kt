package com.kotlin.androidtest.DataBase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kotlin.androidtest.Model.UserItem
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_table")
data class UserEntity(
        @PrimaryKey(autoGenerate = true)
        val id :Int?,
        val login : String,
        val avatar_url : String,
        val notes : String,
):Parcelable
