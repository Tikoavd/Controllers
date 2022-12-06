package com.freelance.controllers.Fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.freelance.controllers.Adapters.ButtonsAdapter
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    var openAddDialog: OpenAddDialogFragment? = null

    private var _binding: FragmentMenuBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return FragmentMenuBinding.inflate(inflater)
            .also { _binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(activity?.findViewById<FragmentContainerView>(R.id.mainFragmentContainer)!!.id,
                HomeFragment()).commit()

        val db = AppDatabase.createDatabase(requireContext())
        val instalDao = db.instalDao()
        val recyclerItems = instalDao.getAll().toMutableList()

        val setAdapter = ButtonsAdapter(recyclerItems)
            .apply {
                openInstalFragment = OpenInstalFragment { fragment ->
                    fragment.menuFragment = this@MenuFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            activity?.findViewById<FragmentContainerView>(R.id.mainFragmentContainer)!!.id,
                            fragment).commit()
                }

                openHomeFragment = OpenHomeFragment {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(activity?.findViewById<FragmentContainerView>(R.id.mainFragmentContainer)!!.id,
                            HomeFragment()).commit()
                }
            }

        with(binding.recycleButtons) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = setAdapter
        }

        binding.logoButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(activity?.findViewById<FragmentContainerView>(R.id.mainFragmentContainer)!!.id,
                    HomeFragment()).commit()

            setAdapter.selected?.fragmentButton?.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#a6a4a4"))
            setAdapter.selected = null
        }

        binding.addButton.setOnClickListener {
            openAddDialog?.openDialog(setAdapter, recyclerItems)
        }
    }
}