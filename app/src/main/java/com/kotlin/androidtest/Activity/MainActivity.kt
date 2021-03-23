package com.kotlin.androidtest.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.androidtest.Adapter.UserAdapter
import com.kotlin.androidtest.DataBase.UserDatabase
import com.kotlin.androidtest.DataBase.UserEntity
import com.kotlin.androidtest.DataBase.UserViewModel
import com.kotlin.androidtest.Model.User
import com.kotlin.androidtest.Model.UserItem
import com.kotlin.androidtest.R
import com.kotlin.androidtest.Service.SellerServices
import com.kotlin.androidtest.utlis.Glob
import com.kotlin.androidtest.utlis.NetworkConnection
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), Glob.SelectedItem {
    private var useradapter: UserAdapter? = null
    val userlist: ArrayList<UserItem> = ArrayList()
    private lateinit var mUserViewModel: UserViewModel
    var page_index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Init()
        searchview.visibility = View.GONE
        //userviewmodel
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

    }

    private fun Init() {
        //START recyclerview
        searchview.visibility = View.GONE
        shimmerFrameLayout.startShimmerAnimation()
        shimmerFrameLayout.visibility = View.VISIBLE
        recycelview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        useradapter = UserAdapter(this, userlist, this, page_index)
        recycelview.adapter = useradapter
        recycelview.setHasFixedSize(true)
        //END

        //START pagination load more items
        useradapter!!.setLoadMoreListener(object :
            UserAdapter.OnLoadMoreListener {
            override fun onLoadMore() {
                recycelview.post(Runnable {
                    page_index = page_index + userlist.size
                    try {
                        getData(page_index, true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }
        })
        //END
        database()
    }

    private fun database() {
        //START check Connection
        val connectionLiveData = NetworkConnection(this)
        connectionLiveData.observe(this, Observer { isConnected ->
            if (isConnected) {
                getData(page_index, false)
            } else {
                //START search query
                searchview.setOnClickListener(View.OnClickListener { searchview.setIconified(false) })
                searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        useradapter!!.filter.filter(newText)
                        return false
                    }
                })
                //END search query
                //END

                //START show local data
                if (userlist.size != 0){
                    searchview.visibility = View.VISIBLE
                    shimmerFrameLayout.visibility = View.GONE
                    mUserViewModel.readAllData.observe(this, Observer {
                        userlist.clear()
                        for (i in 0 until it.size) {
                            val user = UserItem(
                                login = it[i].login,
                                avatar_url = it[i].avatar_url,
                                notes = it[i].notes
                            )
                            userlist.add(user)
                        }
                        Log.d("list", userlist.size.toString())
                        useradapter!!.notifyDataChanged()
                    })
                }else if(userlist.size > 0){
                    shimmerFrameLayout.visibility = View.GONE
                }
                else
                {
                    searchview.visibility = View.GONE
                }
                //END
                Glob().networkDailog(this)

            }
        })
    }

    //START fetch data of userslist
    private fun getData(page_index: Int, isprogress: Boolean) {

        if (isprogress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
        val user = SellerServices.list.listinterface.getlist(page_index)
        user.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                progress_circular.visibility = View.GONE
                searchview.visibility = View.GONE
                shimmerFrameLayout.stopShimmerAnimation()
                shimmerFrameLayout.visibility = View.GONE
                val data = response.body()
                if (data != null) {
                    if (!isprogress) {
                        userlist.clear()
                        useradapter!!.notifyDataSetChanged()
                    }
                    for (i in 0 until data.size) {
                        userlist.add(data[i])
                        //START add data into database
                        val user_name = data[i].login
                        val user_imgae = data[i].avatar_url
                        val user_notes = data[i].notes
                        val user = UserEntity(null, user_name, user_imgae, user_notes)

                        mUserViewModel.addUser(user)
                        //END
                    }
                    useradapter!!.notifyDataChanged()
                } else {
                    shimmerFrameLayout.startShimmerAnimation()
                    shimmerFrameLayout.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                progress_circular.visibility = View.GONE
                Log.d("tagline", t.message.toString())
            }
        })
    }
    //START click listner
    override fun selecteditem(pos: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(Glob().user_data, userlist[pos])
        startActivity(intent)
    }
    //END
}
