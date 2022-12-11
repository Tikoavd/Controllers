package com.freelance.controllers.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.freelance.controllers.Fragments.toEditable
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.HomeEntity

class HomeSettingsAdapter(val units: MutableList<HomeEntity>) :
    RecyclerView.Adapter<HomeSettingsAdapter.HomeSettingsViewHolder>() {

    inner class HomeSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ipAddressEdit = itemView.findViewById<EditText>(R.id.ipAddressEdit)
        val portEdit = itemView.findViewById<EditText>(R.id.portEdit)
        val saveButton = itemView.findViewById<Button>(R.id.saveButton)
        val deleteButton = itemView.findViewById<Button>(R.id.deleteHostButton)

        fun bind(position: Int) {
            saveButton.setOnClickListener {
                val db = AppDatabase.createDatabase(it.context)
                val homeDao = db.homeDao()

                val host = ipAddressEdit.text.toString()
                if (!host.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b".toRegex())) {
                    Toast.makeText(it.context, "Error: Invalid IP Address", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val port = portEdit.text.toString()
                if (!port.matches("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$".toRegex())) {
                    Toast.makeText(it.context, "Error: Invalid Port Number", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                units[position].host = host
                units[position].port = port
                homeDao.updateUnit(units[position])
                notifyItemChanged(position)
            }

            deleteButton.setOnClickListener {
                val db = AppDatabase.createDatabase(it.context)
                val homeDao = db.homeDao()

                if (units.size == 1) {
                    homeDao.deleteUnit(units[0])
                    units.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    homeDao.deleteUnit(units[position])
                    units.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSettingsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycleview_settingspage, parent, false)
        return HomeSettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeSettingsViewHolder, position: Int) {
        holder.ipAddressEdit.text = units[position].host.toEditable()
        holder.portEdit.text = units[position].port.toEditable()
        holder.bind(position)
    }

    override fun getItemCount(): Int = units.size
}