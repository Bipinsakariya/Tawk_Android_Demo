package com.kotlin.androidtest.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kotlin.androidtest.DataBase.ProfileEntity
import com.kotlin.androidtest.DataBase.UserDatabase
import com.kotlin.androidtest.DataBase.UserViewModel
import com.kotlin.androidtest.Model.UserItem
import com.kotlin.androidtest.Model.UserProfile
import com.kotlin.androidtest.R
import com.kotlin.androidtest.Service.SellerServices
import com.kotlin.androidtest.utlis.Glob
import com.kotlin.androidtest.utlis.NetworkConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivity : AppCompatActivity(),View.OnClickListener {

    var userlist: UserItem? = null
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()
    }

    private fun init() {
        btn_userProfileSaveBtnn.setOnClickListener(this)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userlist = intent.getSerializableExtra(Glob().user_data) as UserItem
        database()
    }

    private fun database() {
        //START check Connection
        val connectionLiveData = NetworkConnection(this)
        connectionLiveData.observe(this, Observer { isConnected ->
            if (isConnected) {
                getProfileDetails()
            } else {
                Glob().networkDailog(this)
                //START show local data
                val db = UserDatabase.getDatabase(context = this)
                var isuser = false;
                Thread(Runnable {
                    val data = db.userDao().getProfileFromDB(userlist!!.login)
                    var userProfile = UserProfile()

                    if (data != null) {
                        Log.d("hello", data.toString())

                        isuser = true
                        userProfile = UserProfile(
                            avatar_url = data.avatar_url,
                            name = data.name.toString(),
                            company = data.company.toString(),
                            blog = data.blog.toString(),
                            location = data.location.toString(),
                            followers = data.followers,
                            following = data.following,
                            login = data.login.toString(),
                            notes = data.notes!!
                        )

                    } else {
                        isuser = false
                        Log.d("hello", "data.toString(")

                    }

                    this.runOnUiThread(java.lang.Runnable {
                        if (isuser) {
                            constraintlayout.visibility = View.VISIBLE
                            manageDataView(userProfile)
                        }else{
                            constraintlayout.visibility = View.GONE
                            val builder: AlertDialog.Builder
                            builder = AlertDialog.Builder(this)
                            builder.setMessage(this.resources.getString(R.string.data_is_not_avaliable)).
                            setTitle(resources.getString(R.string.alert))
                                .setCancelable(false).setPositiveButton(resources.getString(R.string.ok),
                                    DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
                            val alert = builder.create()
                            if (!alert.isShowing) {
                                alert.show()
                            }
                        }
                    })
                }).start()

            }
        })
    }

    //START update notes in database
    private fun updateItem() {
        val db = UserDatabase.getDatabase(context = this)
        Thread(Runnable {
            db.userDao().updateUserProfile(
                note = edittext_notes.text.toString(),
                login = userlist!!.login
            )
        }).start()

        Thread(Runnable {
            db.userDao().updateUsernotes(
                note = edittext_notes.text.toString(),
                login = userlist!!.login
            )
        }).start()

    }
    //END

    //START fetch data of profile details
    private fun getProfileDetails() {
        val user = SellerServices.list.listinterface.getprofilelist(userlist!!.login)
        user.enqueue(object : Callback<UserProfile> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                val data = response.body()
                constraintlayout.visibility = View.VISIBLE
                shimmerFrameLayoutprofile.stopShimmerAnimation()
                shimmerFrameLayoutprofile.visibility = View.GONE
                if (data != null) {
                    manageDataView(data)
                    insertdata(data)
                }
            }
            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                constraintlayout.visibility = View.GONE
                shimmerFrameLayoutprofile.startShimmerAnimation()
                shimmerFrameLayoutprofile.visibility = View.VISIBLE
                Log.d("tagline", t.message.toString())
            }

        })
    }
    //END

    //START set data into the activity
    private fun manageDataView(data: UserProfile?) {
        try {
            if (data != null) {
                textview_username.text = data.name
                textview_name.text = data.login
                textview_followers.text =
                    getString(R.string.txt_followers) + data.followers.toString()
                textview_following.text =
                    getString(R.string.txt_following) + data.following.toString()
                if (data.location.isNullOrEmpty()) {
                    textview_Location.visibility = View.GONE
                } else {
                    textview_Location.visibility = View.VISIBLE
                    textview_Location.text =
                        getString(R.string.txt_location) + data.location
                }
                if (data.company.isNullOrEmpty()) {
                    textview_Company.visibility = View.GONE
                } else {
                    textview_Company.visibility = View.VISIBLE
                    textview_Company.text =
                        getString(R.string.txt_company) + data.company
                }
                if (data.blog.isNullOrEmpty()) {
                    textview_Blog.visibility = View.GONE
                } else {
                    textview_Blog.visibility = View.VISIBLE
                    textview_Blog.text =
                        getString(R.string.txt_blog) + data.blog
                }
                if (data.location.isNullOrEmpty() && data.company.isNullOrEmpty() && data.blog.isNullOrEmpty()) {
                    infoCardView.visibility = View.INVISIBLE
                }
                try {
                    Glide.with(this).load(data.avatar_url).into(imageView)

                } catch (e: Exception) {
                    Log.d("data", e.toString())
                }

                if (!data.notes.isNullOrEmpty()) {
                    edittext_notes.setText(data.notes)
                }
            } else {
                constraintlayout.visibility = View.GONE
                shimmerFrameLayoutprofile.startShimmerAnimation()
                shimmerFrameLayoutprofile.visibility = View.VISIBLE

            }
        } catch (e: Exception) {
            Log.d("dsa", e.toString())
        }
    }
    //END
    //START store data into the database
    private fun insertdata(data: UserProfile) {
        val user_name_login = data.login
        val user_imgae = data.avatar_url
        val user_name = data.name
        val user_blog = data.blog
        val user_company = data.company
        val user_location = data.location
        val user_followers = data.followers
        val user_following = data.following
        val user_notes = edittext_notes.text.toString()

        val user = ProfileEntity(
            null, user_name, user_name_login, user_imgae, user_blog, user_company, user_location,
            user_followers, user_following, user_notes
        )
        mUserViewModel.addUserProfile(user)
    }
    //END
    //START onclick method
    override fun onClick(view: View) {
        when(view){
            //START save button click
            btn_userProfileSaveBtnn ->
            {
                updateItem()
                val builder: AlertDialog.Builder
                builder = AlertDialog.Builder(this)
                builder.setMessage(this.resources.getString(R.string.Notes)).
                setTitle(resources.getString(R.string.alert))
                    .setCancelable(false).setPositiveButton(resources.getString(R.string.ok),
                        DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
                val alert = builder.create()
                if (!alert.isShowing) {
                    alert.show()
                }
            }
            //END
        }
    }
    //END
}


