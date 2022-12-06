package com.freelance.controllers.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.freelance.controllers.Fragments.RepeatSettingsRequest
import com.freelance.controllers.Fragments.toEditable
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.PlayerEntity

class SettingsAdapter(val players: MutableList<PlayerEntity>) :
    RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {
    var repeatSettingsRequest: RepeatSettingsRequest? = null

    inner class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ipAddressEdit = itemView.findViewById<EditText>(R.id.ipAddressEdit)
        val portEdit = itemView.findViewById<EditText>(R.id.portEdit)
        val saveButton = itemView.findViewById<Button>(R.id.saveButton)
        val deleteButton = itemView.findViewById<Button>(R.id.deleteHostButton)

        fun bind(position: Int) {
            saveButton.setOnClickListener {
                val db = AppDatabase.createDatabase(it.context)
                val playerDao = db.playerDao()

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

                players[position].host = host
                players[position].port = port
                playerDao.updatePlayer(players[position])
                notifyItemChanged(position)
            }

            deleteButton.setOnClickListener {
                val db = AppDatabase.createDatabase(it.context)
                val playerDao = db.playerDao()
                if (players.size == 1) {
                    playerDao.deletePlayer(players[0])
                    players.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    playerDao.deletePlayer(players[position])
                    players.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycleview_settingspage, parent, false)
        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.ipAddressEdit.text = players[position].host.toEditable()
        holder.portEdit.text = players[position].port.toEditable()
        holder.bind(position)
    }

    override fun getItemCount(): Int = players.size
}