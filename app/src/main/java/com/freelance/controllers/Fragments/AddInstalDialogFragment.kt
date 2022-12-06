package com.freelance.controllers.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.freelance.controllers.Adapters.ButtonsAdapter
import com.freelance.controllers.R
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalDao
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.InstalType

class AddInstalDialogFragment : DialogFragment() {
    var adapter: ButtonsAdapter? = null
    var repeatRequest: RepeatRequest? = null
    var showChoiceDialog: ShowChoiceDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            //val inflater = requireActivity().layoutInflater
            val editInstalName = EditText(context)
            editInstalName.setEms(10)
            editInstalName.inputType = 0x00000061

            builder.setView(editInstalName)
                .setPositiveButton("Создать") { dialog, id ->
                    editInstalName.run {
                        if (text.isNotBlank()) {
                            showChoiceDialog?.showChoiceDialog(text.toString())
                        }
                    }
                    getDialog()?.cancel()
                }
                .setNegativeButton("Отмена") { dialog, id ->
                    getDialog()?.cancel()
                }
                .setTitle("Название инсталяции:")

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}