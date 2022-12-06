package com.freelance.controllers.Fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.freelance.controllers.Adapters.SettingsAdapter
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.PlayerEntity
import com.freelance.controllers.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    var inFullScreen: Boolean = false
    var menuFragment: MenuFragment? = null

    var _instalEntity: InstalEntity? = null
    private val instalEntity
        get() = _instalEntity!!

    var _players: MutableList<PlayerEntity>? = null
    private val players
        get() = _players!!

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentSettingsBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.createDatabase(requireContext())
        val playerDao = db.playerDao()

        binding.instalationEdit.text = instalEntity.name.toEditable()
        binding.videoNameEdit.text = instalEntity.video.toEditable()

        val setAdapter = SettingsAdapter(players)

        with(binding.settingsRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = setAdapter
        }

        binding.addHostButton.setOnClickListener {
            playerDao.addPlayer(PlayerEntity(instalEntity.id))
            players.clear()
            players.addAll(playerDao.getPlayers(instalEntity.id))
            setAdapter.notifyItemInserted(players.size)
        }

        binding.saveInstalButton.setOnClickListener {
            instalEntity.name = binding.instalationEdit.text.toString()
            instalEntity.video = binding.videoNameEdit.text.toString()
            val instalDao = db.instalDao()
            instalDao.updateInstal(instalEntity)
        }

        binding.backToMainButton.setOnClickListener {
            val fragment = MainFragment().apply {
                _instalEntity = this@SettingsFragment.instalEntity
                inFullScreen = this@SettingsFragment.inFullScreen
                menuFragment = this@SettingsFragment.menuFragment
            }
            if (!inFullScreen) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment).commit()
            }
            else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFullScreenContainer, fragment).commit()
            }
        }
    }
}

fun String.toEditable(): Editable =
    Editable.Factory.getInstance().newEditable(this)