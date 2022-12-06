package com.freelance.controllers

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.freelance.controllers.Fragments.*
import com.freelance.controllers.Room.AppDatabase
import com.freelance.controllers.Room.InstalEntity
import com.freelance.controllers.Room.InstalType
import com.freelance.controllers.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val password = getSharedPreferences("password", Context.MODE_PRIVATE)
            .getString("password", "123456")


        val editPassword = EditText(this)
        editPassword.setEms(10)
        editPassword.inputType = 0x00000061

        val builder = AlertDialog.Builder(this)
        builder.setView(editPassword)
            .setTitle("Введите пароль")
            .setPositiveButton("Ок") { dialog, id ->
                val enteredPassword = editPassword.text.toString()
                if (enteredPassword.isNotBlank() && enteredPassword == password) {
                    val menuFragment = MenuFragment()
                    menuFragment.openAddDialog = OpenAddDialogFragment { setAdapter, items ->
                        AddInstalDialogFragment().apply {
                            adapter = setAdapter
                            repeatRequest = RepeatRequest {
                                val db = AppDatabase.createDatabase(this@MainActivity)
                                val instalDao = db.instalDao()
                                items.clear()
                                items.addAll(instalDao.getAll().toMutableList())
                            }
                            showChoiceDialog = ShowChoiceDialog { name ->
                                val typeNames = Array<String>(InstalType.values().size) { "" }
                                typeNames[0] = "BrightSign"
                                typeNames[1] = "Розетка"
                                var choicedItem = typeNames[0]

                                val db = AppDatabase.createDatabase(this@MainActivity)
                                val instalDao = db.instalDao()


                                val builder = AlertDialog.Builder(this@MainActivity)

                                builder
                                    .setTitle("Выберите тип")
                                    .setSingleChoiceItems(typeNames, 0) { dialog, which ->
                                        choicedItem = typeNames[which]
                                    }
                                    .setPositiveButton("Ок") { dialog, id ->
                                        val instalType = when (choicedItem) {
                                            "BrightSign" -> InstalType.Default
                                            "Розетка" -> InstalType.Socket
                                            else -> InstalType.Default
                                        }
                                        val instalEntity = InstalEntity(name, instalType)

                                        instalDao.insertInstal(instalEntity)
                                        repeatRequest?.repeatRequest()
                                        adapter?.notifyDataSetChanged()
                                        getDialog()?.cancel()
                                    }
                                    .create().show()
                            }
                            show(supportFragmentManager, "AddInstal")
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainMenuContainer.id, menuFragment).commit()
                    dialog.cancel()

                } else {
                    dialog.cancel()
                    Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        builder.create().show()
    }
}