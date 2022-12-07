package com.freelance.controllers.Fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import com.freelance.controllers.R
import com.freelance.controllers.Request.UDPSender
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.InstalType
import com.freelance.controllers.Room.PlayerEntity
import com.freelance.controllers.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    var playing: Boolean = false
    var inFullScreen: Boolean = false
    var menuFragment: MenuFragment? = null

    var _instalEntity: InstalEntity? = null
    private val instalEntity
        get() = _instalEntity!!

    lateinit var players: MutableList<PlayerEntity>


    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentMainBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.videoName.text = instalEntity.video
        if (!menuFragment!!.inAdminMode) binding.settingsButton.visibility = View.INVISIBLE

        if (playing) {
            binding.playimg.visibility = View.INVISIBLE
            binding.pauseimg.visibility = View.VISIBLE
        }

        val db = AppDatabase.createDatabase(requireContext())
        val playerDao = db.playerDao()
        players = playerDao.getPlayers(instalEntity.id).toMutableList()

        if (players.size == 0) {
            playerDao.addPlayer(PlayerEntity(instalEntity.id))
            players.addAll(playerDao.getPlayers(instalEntity.id).toMutableList())
        }

        binding.fullScreenButton.setOnClickListener {
            if (!inFullScreen) {
                val fullScreenFragment = MainFragment()
                fullScreenFragment.playing = this.playing
                fullScreenFragment._instalEntity = this.instalEntity
                fullScreenFragment.menuFragment = this.menuFragment
                fullScreenFragment.inFullScreen = true

                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(this)
                    .remove(menuFragment!!)
                    .replace(
                        requireActivity().findViewById<FragmentContainerView>(R.id.mainFullScreenContainer).id,
                        fullScreenFragment
                    )
                    .commit()
                return@setOnClickListener
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .replace(
                    requireActivity().findViewById<FragmentContainerView>(R.id.mainMenuContainer).id,
                    menuFragment!!
                )
                .commit()
        }

        binding.settingsButton.setOnClickListener {
            val settingsFragment = SettingsFragment()
            with(settingsFragment) {
                _instalEntity = instalEntity
                _players = players
                inFullScreen = this@MainFragment.inFullScreen
                menuFragment = this@MainFragment.menuFragment
            }
            if (!inFullScreen) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, settingsFragment).commit()
                return@setOnClickListener
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFullScreenContainer, settingsFragment).commit()
        }

        binding.playPauseButton.setOnClickListener {
            if (playing) {
                playing = false
                binding.playimg.visibility = View.VISIBLE
                binding.pauseimg.visibility = View.INVISIBLE

                for (player in players) {
                    val uri =
                        Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("PAUSE")}")
                    UDPSender().SendTo(requireContext(), uri)
                }
                return@setOnClickListener
            }

            playing = true
            binding.playimg.visibility = View.INVISIBLE
            binding.pauseimg.visibility = View.VISIBLE

            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("PLAY")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.repeatButton.setOnClickListener {
            binding.playimg.visibility = View.INVISIBLE
            binding.pauseimg.visibility = View.VISIBLE
            playing = true

            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("REP")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.previousButton.setOnClickListener {
            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("PREV")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.nextButton.setOnClickListener {
            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("NEXT")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.minusButton.setOnClickListener {
            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("VOL-")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.plusButton.setOnClickListener {
            for (player in players) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("VOL+")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }
    }
}