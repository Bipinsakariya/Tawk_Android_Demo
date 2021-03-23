package com.kotlin.androidtest.utlis

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import com.kotlin.androidtest.R

class Glob {
    val user_data: String = "User_data"

    //START network Dailog
    fun networkDailog(context: Context) {
        val builder: AlertDialog.Builder
        builder = AlertDialog.Builder(context)
        builder.setMessage(context.resources.getString(R.string.network_msg))
            .setTitle(context.resources.getString(R.string.networkdialod))
            .setCancelable(false).setPositiveButton(context.resources.getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
        val alert = builder.create()
        if (!alert.isShowing) {
            alert.show()
        }
    }
    //END


    interface SelectedItem {
        fun selecteditem(pos: Int)
    }

}