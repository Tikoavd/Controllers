package com.freelance.controllers.Fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.freelance.controllers.R
import com.freelance.controllers.Request.UDPSender
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.PlayerEntity
import com.freelance.controllers.databinding.FragmentProjectorBinding
import com.freelance.controllers.databinding.FragmentSocketBinding

class ProjectorFragment : Fragment() {
    var menuFragment: MenuFragment? = null

    var _instalEntity: InstalEntity? = null
    private val instalEntity
        get() = _instalEntity!!

    lateinit var players: MutableList<PlayerEntity>

    private var _binding: FragmentProjectorBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentProjectorBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.projectorName.text = instalEntity.video
        if (!menuFragment!!.inAdminMode) binding.settingsProjectorButton.visibility = View.INVISIBLE

        val db = AppDatabase.createDatabase(requireContext())
        val playerDao = db.playerDao()
        players = playerDao.getPlayers(instalEntity.id).toMutableList()

        if (players.size == 0) {
            playerDao.addPlayer(PlayerEntity(instalEntity.id, "192.168.0.127"))
            players.addAll(playerDao.getPlayers(instalEntity.id).toMutableList())
        }

        binding.settingsProjectorButton.setOnClickListener {
            val settingsFragment = SettingsFragment()
            with(settingsFragment) {
                _instalEntity = instalEntity
                _players = players
                menuFragment = this@ProjectorFragment.menuFragment
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, settingsFragment).commit()
            return@setOnClickListener
        }

        binding.onProjectorButton.setOnClickListener {
            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("0x7E3030303020310D")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.offProjectorButton.setOnClickListener {
            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("0x7E3030303020300D")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }
    }
}