package com.freelance.controllers.Fragments.Interfaces

fun interface OpenPasswordDialog {
    fun openDialog(action: (correct: Boolean) -> Unit)
}