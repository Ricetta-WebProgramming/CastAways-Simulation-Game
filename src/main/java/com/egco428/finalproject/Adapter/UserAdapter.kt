package com.egco428.finalproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.egco428.finalproject.Model.User
import com.egco428.finalproject.R
import java.time.format.DateTimeFormatter

class UserViewHolder(itemView: View, itemOnClickListener: ItemOnClickListener) :
    RecyclerView.ViewHolder(itemView) {

    // View Variable in each row
    var nameText: TextView



    init {
        nameText = itemView.findViewById(R.id.nameText)
        // Add LongClickListener to viewholder
        itemView.setOnLongClickListener { view ->
            view.animate().setDuration(400).alpha(0F).withEndAction(Runnable {
                // Call function in MainActivity (And send position of holder)
                itemOnClickListener.onLongClick(adapterPosition)
                view.alpha = 1F

            })
            return@setOnLongClickListener true
        }

    }


}



class UserAdapter(
    private val userList: MutableList<User>,
    private val mContext: Context,
    private val itemOnClickListener: ItemOnClickListener
) : RecyclerView.Adapter<UserViewHolder>() {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(mContext)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


        // Set Text in Viewholder
        holder.nameText.text = userList[position].name



    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = inflater.inflate(R.layout.row, parent, false)
        return UserViewHolder(itemView, itemOnClickListener)

    }


}


