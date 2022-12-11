package com.freelance.controllers.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.freelance.controllers.Adapters.HomeSettingsAdapter
import com.freelance.controllers.Fragments.Interfaces.AdminMode
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.HomeEntity
import com.freelance.controllers.Room.InstalType
import com.freelance.controllers.databinding.FragmentHomeSettingsBinding
import com.freelance.controllers.databinding.FragmentSocketBinding


class HomeSettingsFragment : Fragment() {
    var adminMode: AdminMode? = null

    private var _binding: FragmentHomeSettingsBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentHomeSettingsBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToHomeButton.setOnClickListener {
            val homeFragment = HomeFragment()
                .apply {
                    adminMode = this@HomeSettingsFragment.adminMode
                    inAdminMode = true
                }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer,
                    homeFragment)
                .commit()
        }

        val db = AppDatabase.createDatabase(requireContext())
        val homeDao = db.homeDao()

        val players = homeDao.getPlayers()
        val sockets = homeDao.getSockets()
        val projectors = homeDao.getProjectors()

        val playersAdapter = HomeSettingsAdapter(players)
        val socketsAdapter = HomeSettingsAdapter(sockets)
        val projectorsAdapter = HomeSettingsAdapter(projectors)

        with(binding.playerRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playersAdapter
        }

        with(binding.socketRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = socketsAdapter
        }

        with(binding.projectorRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = projectorsAdapter
        }

        binding.addPlayerButton.setOnClickListener {
            homeDao.addUnit(HomeEntity(InstalType.Default))
            players.clear()
            players.addAll(homeDao.getPlayers())
            playersAdapter.notifyItemInserted(players.size)
        }

        binding.addSocketButton.setOnClickListener {
            homeDao.addUnit(HomeEntity(InstalType.Socket))
            sockets.clear()
            sockets.addAll(homeDao.getSockets())
            socketsAdapter.notifyItemInserted(sockets.size)
        }

        binding.addProjectorButton.setOnClickListener {
            homeDao.addUnit(HomeEntity(InstalType.Projector))
            projectors.clear()
            projectors.addAll(homeDao.getProjectors())
            projectorsAdapter.notifyItemInserted(projectors.size)
        }
    }
}