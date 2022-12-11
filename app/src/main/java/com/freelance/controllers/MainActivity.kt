package com.freelance.controllers

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.freelance.controllers.Fragments.*
import com.freelance.controllers.Fragments.Interfaces.OpenAddDialogFragment
import com.freelance.controllers.Fragments.Interfaces.OpenPasswordDialog
import com.freelance.controllers.Fragments.Interfaces.RepeatRequest
import com.freelance.controllers.Fragments.Interfaces.ShowChoiceDialog
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

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

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
                    typeNames[2] = "Проектор"
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
                                "Проектор" -> InstalType.Projector
                                else -> InstalType.Default
                            }
                            val instalEntity = InstalEntity(name, instalType, choicedItem)

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

        HomeFragment.openPasswordDialog = OpenPasswordDialog { action ->
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
                        action(true)
                    } else {
                        dialog.cancel()
                        Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT)
                            .show()
                        action(false)
                    }
                }
            builder.create().show()
        }
    }
}