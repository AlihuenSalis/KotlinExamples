package com.example.applicationinkotlin

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class Adapter(val listUser: List<InfoUser>): RecyclerView.Adapter<Adapter.UserHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_adapter_user, parent, false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int = listUser.size

    class UserHolder(view:View):RecyclerView.ViewHolder(view) {

        private val name = view.findViewById<TextView>(R.id.name)
        private val username = view.findViewById<TextView>(R.id.username)
        private val age = view.findViewById<TextView>(R.id.age)
        private val img = view.findViewById<ImageView>(R.id.img)

        fun bind(listUser: InfoUser){
            name.text = listUser.name
            username.text = listUser.surname
            age.text = listUser.age.toString()
            Glide.with(img.context).load(listUser.image).into(img)
        }

    }
}