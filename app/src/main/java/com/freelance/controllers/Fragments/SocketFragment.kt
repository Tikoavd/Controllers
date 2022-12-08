package com.freelance.controllers.Fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import com.freelance.controllers.R
import com.freelance.controllers.Retrofit.RetrofitBuilder
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.PlayerEntity
import com.freelance.controllers.databinding.FragmentMenuBinding
import com.freelance.controllers.databinding.FragmentSocketBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SocketFragment : Fragment() {
    var menuFragment: MenuFragment? = null

    var _instalEntity: InstalEntity? = null
    private val instalEntity
        get() = _instalEntity!!

    lateinit var players: MutableList<PlayerEntity>

    private var _binding: FragmentSocketBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentSocketBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.socketName.text = instalEntity.video
        if (!menuFragment!!.inAdminMode) binding.settingsButton.visibility = View.INVISIBLE

        val db = AppDatabase.createDatabase(requireContext())
        val playerDao = db.playerDao()
        players = playerDao.getPlayers(instalEntity.id).toMutableList()

        if (players.size == 0) {
            playerDao.addPlayer(PlayerEntity(instalEntity.id, "192.168.0.127"))
            players.addAll(playerDao.getPlayers(instalEntity.id).toMutableList())
        }

        binding.settingsButton.setOnClickListener {
            val settingsFragment = SettingsFragment()
            with(settingsFragment) {
                _instalEntity = instalEntity
                _players = players
                menuFragment = this@SocketFragment.menuFragment
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, settingsFragment).commit()
            return@setOnClickListener
        }

        binding.onSocketButton.setOnClickListener {
            for (player in players) {
                RetrofitBuilder.getApiService("http://${player.host}").socketOn().enqueue(
                    object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (!response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Request is not successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(requireContext(), "Request Failed", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                )
            }
        }

        binding.offSocketButton.setOnClickListener {
            for (player in players) {
                RetrofitBuilder.getApiService("http://${player.host}").socketOff().enqueue(
                    object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (!response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Request is not successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(requireContext(), "Request Failed", Toast.LENGTH_SHORT)
                                .show()
                            println(t)
                        }
                    }
                )
            }
        }
    }
}