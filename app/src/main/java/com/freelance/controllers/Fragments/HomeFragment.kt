package com.freelance.controllers.Fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.freelance.controllers.Fragments.Interfaces.AdminMode
import com.freelance.controllers.Fragments.Interfaces.OpenPasswordDialog
import com.freelance.controllers.R
import com.freelance.controllers.Request.UDPSender
import com.freelance.controllers.Retrofit.RetrofitBuilder
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalType
import com.freelance.controllers.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    var inAdminMode = false
    var adminMode: AdminMode? = null

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
        val homeDao = db.homeDao()

        val players = homeDao.getPlayers()
        val sockets = homeDao.getSockets()
        val projectors = homeDao.getProjectors()

        if (!inAdminMode) binding.homeSettingsButton.visibility = View.INVISIBLE

        binding.homeSettingsButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer,
                    HomeSettingsFragment()
                        .apply { adminMode = this@HomeFragment.adminMode })
                .commit()
        }

        binding.onButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())

            builder
                .setMessage("Вы действительно хотите включит приборы?")
                .setPositiveButton("Да") { dialog, id ->
                    for (player in players) {
                        val uri =
                            Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("DISPON")}")
                        UDPSender().SendTo(requireContext(), uri)
                    }

                    for (socket in sockets) {
                        RetrofitBuilder.getApiService("http://${socket.host}/").socketOn().enqueue(
                            object : Callback<String> {
                                override fun onResponse(call: Call<String>, response: Response<String>) {
                                    if (!response.isSuccessful) {
                                        Toast.makeText(
                                            requireActivity().applicationContext,
                                            "Request is not successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return
                                    }
                                }

                                override fun onFailure(call: Call<String>, t: Throwable) {

                                }

                            }
                        )
                    }

                    for (projector in projectors) {
                        val uri = Uri.parse("udp://${projector.host}:${projector.port}/${Uri.encode("0x7E3030303020310D")}")
                        UDPSender().SendTo(requireContext(), uri)
                    }

                    dialog.cancel()
                }
                .setNegativeButton("Нет") { dialog, id ->
                    dialog.cancel()
                }.create().show()
        }

        binding.offButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())

            builder
                .setMessage("Вы действительно хотите выключит приборы?")
                .setPositiveButton("Да") { dialog, id ->
                    for (player in players) {
                        val uri =
                            Uri.parse("udp://${player.host}:${player.port}/${Uri.encode("DISPOFF")}")
                        UDPSender().SendTo(requireContext(), uri)
                    }

                    for (socket in sockets) {
                        RetrofitBuilder.getApiService("http://${socket.host}/").socketOff().enqueue(
                            object : Callback<String> {
                                override fun onResponse(call: Call<String>, response: Response<String>) {
                                    if (!response.isSuccessful) {
                                        Toast.makeText(
                                            requireActivity().applicationContext,
                                            "Request is not successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return
                                    }
                                }

                                override fun onFailure(call: Call<String>, t: Throwable) {

                                }

                            }
                        )
                    }

                    for (projector in projectors) {
                        val uri = Uri.parse("udp://${projector.host}:${projector.port}/${Uri.encode("0x7E3030303020300D")}")
                        UDPSender().SendTo(requireContext(), uri)
                    }

                    dialog.cancel()
                }
                .setNegativeButton("Нет") { dialog, id ->
                    dialog.cancel()
                }.create().show()

        }

        binding.switchAdminMode.isChecked = inAdminMode

        binding.switchAdminMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                openPasswordDialog?.openDialog { isCorrect ->
                    if (isCorrect) {
                        adminMode?.state(isChecked)
                        binding.homeSettingsButton.visibility = View.VISIBLE
                    }
                    else binding.switchAdminMode.isChecked = false
                }
            } else {
                adminMode?.state(isChecked)
                binding.homeSettingsButton.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        var openPasswordDialog: OpenPasswordDialog? = null
    }
}