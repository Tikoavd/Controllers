package com.freelance.controllers.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.freelance.controllers.R
import com.freelance.controllers.Request.UDPSender
import com.freelance.controllers.Request.bytesToHex
import com.freelance.controllers.Request.hexToBytes
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.PlayerEntity
import com.freelance.controllers.databinding.FragmentProjectorBinding
import kotlinx.coroutines.*
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

class ProjectorFragment : Fragment() {
    val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        println(throwable.message)
    })

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
                coroutineScope.launch {
                    val socket = coroutineScope.async {
                        val inetAddress = InetAddress.getByName(player.host)
                        Socket(inetAddress, player.port.toInt())
                    }

                    val writer = DataOutputStream(socket.await().getOutputStream())
                    writer.write("2531504f575220310d".hexToBytes())
                    writer.flush()
                    socket.await().close()
                }
            }
        }

        binding.offProjectorButton.setOnClickListener {
            for (player in players) {
                coroutineScope.launch {
                    val socket = coroutineScope.async {
                        val inetAddress = InetAddress.getByName(player.host)
                        Socket(inetAddress, player.port.toInt())
                    }

                    val writer = DataOutputStream(socket.await().getOutputStream())
                    writer.write("2531504f575220300d".hexToBytes())
                    writer.flush()
                    socket.await().close()
                }
            }
        }
    }
}
