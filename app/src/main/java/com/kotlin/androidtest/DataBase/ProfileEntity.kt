package com.kotlin.androidtest.DataBase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "profile_table")
data class ProfileEntity(
        @PrimaryKey(autoGenerate = true)
        val id :Int?,
        val name: String? =null,
        val login : String? =null,
        val avatar_url : String? =null,
        val blog: String?=null,
        val company: String?=null,
        val location: String?=null,
        val followers: Int = -1,
        val following: Int = -1,
        val notes : String ? =null
):Parcelable
