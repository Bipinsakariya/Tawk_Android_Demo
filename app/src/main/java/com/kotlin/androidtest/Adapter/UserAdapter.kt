package com.kotlin.androidtest.Adapter

import android.content.Context
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.kotlin.androidtest.DataBase.UserEntity
import com.kotlin.androidtest.Model.User
import com.kotlin.androidtest.Model.UserItem
import com.kotlin.androidtest.R
import com.kotlin.androidtest.utlis.Glob
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(
    val context: Context,
    val userlist: ArrayList<UserItem>,
    val selectedItem: Glob.SelectedItem,
    var pageIndex: Int

) : RecyclerView.Adapter<UserAdapter.MyViewHolder>(), Filterable {
    private var loadMoreListener: OnLoadMoreListener? = null
    var isLoading = false
    private var isMoreDataAvailable = true
    //START searchview
    var countryFilterList = ArrayList<UserItem>()

    init {
        countryFilterList = userlist
    }
    //END

    //START matrix for inverted image
    companion object {
        val negative = floatArrayOf(
            -1.0f, .0f, .0f, .0f, 255.0f,
            .0f, -1.0f, .0f, .0f, 255.0f,
            .0f, .0f, -1.0f, .0f, 255.0f,
            .0f, .0f, .0f, 1.0f, .0f
        )
    }
    //END

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageview = itemView.findViewById<ImageView>(R.id.image)
        val textview = itemView.findViewById<TextView>(R.id.textview_username)
        val imageview_notes = itemView.findViewById<ImageView>(R.id.imageview_notes)
        val itemcontainer = itemView.findViewById(R.id.itemcontainer) as ConstraintLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = countryFilterList[position]
        holder.textview.text = user.login
        if (!user.notes.isNullOrEmpty()) {
            holder.imageview_notes.visibility = View.VISIBLE
        } else {
            holder.imageview_notes.visibility = View.GONE
        }

        Glide.with(context)
            .load(user.avatar_url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(GlideRequestListener(position + 1))
            .circleCrop()
            .thumbnail(1.0f)
            .into(holder.imageview)

        holder.itemcontainer.setOnClickListener {
            selectedItem.selecteditem(position)
        }
        //START for pagination
        if (userlist.size >= 0) {
            if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading) {
                isLoading = true
                loadMoreListener?.onLoadMore()
                pageIndex = pageIndex + 1
            }
        }
        //END
    }

    override fun getItemCount(): Int {
        return countryFilterList.size
    }
    //START for image inverted process
    class GlideRequestListener(private val position: Int) : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            return if (resource != null) {
                if (position % 4 == 0) {
                    resource.colorFilter = ColorMatrixColorFilter(negative)
                }
                target?.onResourceReady(
                    resource,
                    DrawableCrossFadeTransition(1000, isFirstResource)
                )
                true
            } else {
                false
            }
        }
    }
    //END

    //START for pagination
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        this.loadMoreListener = loadMoreListener
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
        isLoading = false
    }
    //END

    //START searchview filter for local search
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList = userlist
                } else {
                    val resultList = ArrayList<UserItem>()
                    for (row in userlist) {
                        if (row.login.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    countryFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<UserItem>
                notifyDataSetChanged()
            }
        }
    }
    //END
}




