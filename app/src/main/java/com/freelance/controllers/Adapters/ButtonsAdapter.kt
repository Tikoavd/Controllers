package com.freelance.controllers.Adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.freelance.controllers.Fragments.*
import com.freelance.controllers.Fragments.Interfaces.OpenHomeFragment
import com.freelance.controllers.Fragments.Interfaces.OpenInstalFragment
import com.freelance.controllers.Fragments.Interfaces.OpenSocketFragment
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.InstalType

class ButtonsAdapter(val items: MutableList<InstalEntity>) : RecyclerView.Adapter<ButtonsAdapter.ButtonsViewHolder>(){
    var selected: ButtonsViewHolder? = null
    var openInstalFragment: OpenInstalFragment? = null
    var openSocketFragment: OpenSocketFragment? = null
    var openHomeFragment: OpenHomeFragment? = null

    inner class ButtonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fragmentButton = itemView.findViewById<Button>(R.id.fragmentButton)
        val deleteButton = itemView.findViewById<Button>(R.id.deleteButton)

        fun bind(position: Int) {
            fragmentButton.setOnClickListener {
                selected?.fragmentButton?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#a6a4a4"))
                selected = this
                fragmentButton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)

                val instalEntity = this@ButtonsAdapter.items[position]

                when (instalEntity.type) {
                    InstalType.Default -> {
                        val fragment = MainFragment().apply {
                            _instalEntity = instalEntity
                        }
                        openInstalFragment?.openFragment(fragment)
                    }
                    InstalType.Socket -> {
                        val fragment = SocketFragment().apply {
                            _instalEntity = instalEntity
                        }
                        openSocketFragment?.openSocketFragment(fragment)
                    }
                }
            }

            deleteButton.visibility = View.INVISIBLE

            deleteButton.setOnClickListener {
                val db = AppDatabase.createDatabase(itemView.context)
                val instalDao = db.instalDao()
                val instalEntity = this@ButtonsAdapter.items[position]

                val playerDao = db.playerDao()
                playerDao.deletePlayers(instalEntity.id)

                instalDao.delete(instalEntity)
                this@ButtonsAdapter.items.removeAt(position)
                notifyItemRemoved(position)

                if (selected == this) {
                    openHomeFragment?.openHome()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview_mainpage, parent, false)
        return ButtonsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ButtonsViewHolder, position: Int) {
        holder.fragmentButton.text = items[position].name
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}