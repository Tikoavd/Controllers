package com.freelance.controllers.Fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.freelance.controllers.R
import com.freelance.controllers.Request.UDPSender
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentHomeBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.createDatabase(requireContext())
        val playerDao = db.playerDao()
        val allPlayers = playerDao.getAllPlayers()

        binding.onButton.setOnClickListener {
            for (player in allPlayers) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("DISPON")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }

        binding.offButton.setOnClickListener {
            for (player in allPlayers) {
                val uri = Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("DISPOFF")}")
                UDPSender().SendTo(requireContext(), uri)
            }
        }
    }
}