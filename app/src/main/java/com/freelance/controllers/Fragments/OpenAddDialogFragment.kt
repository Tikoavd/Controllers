package com.freelance.controllers.Fragments

import com.freelance.controllers.Adapters.ButtonsAdapter
import com.freelance.controllers.Room.InstalEntity

fun interface OpenAddDialogFragment {
    fun openDialog(adapter: ButtonsAdapter, items: MutableList<InstalEntity>)
}