package com.dayi.contactlist.models

import android.app.Person
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelAddContact(val viewModelContacts: ViewModelContacts) : ViewModel() {

    private var isPerson by mutableStateOf(true) // Tracks contact type
    private var isAddressOpen by mutableStateOf(true)
    private var isFamily by mutableStateOf(false)
    private var opinionRating by mutableStateOf(0)
    private var daysOpen = mutableStateListOf(true, true, true, true, true, true, true)
    var _isPerson: Boolean
        get() = isPerson
        set(value) {
            isPerson = value
            setContactType() // Update the contact model while keeping existing values
        }
    var _isAddressOpen : Boolean
        get() = isAddressOpen
        set(value) {isAddressOpen = value}
    var _isFamily : Boolean
        get() = isFamily
        set(value) {
            isFamily = value}
    var _opinionRating : Int
        get() = opinionRating
        set(value) {
            opinionRating = value
        }
    private var newContact = mutableStateOf<ContactModel>(ContactModel(viewModelContacts.getUniqueId()))
    var _newContact: MutableState<ContactModel>
        get() = newContact
        set(value) {
            newContact.value = value.value // Ensure MutableState is updated properly

        }
    var _daysOpen
        get() = daysOpen
        set(value) {
            daysOpen = value
        }

    init {
        setContactType()
    }

    // Toggle the contact type without losing shared ContactModel parameters
    private fun setContactType() {
        val current = newContact.value // Get the existing contact model

        newContact.value = if (isPerson) {
            PersonModel(
                id = current._id,
                name = current._name,
                email = current._email,
                phoneNumber = current._phoneNumber,
                addressStreet = current._addressStreet,
                addressCity = current._addressCity,
                addressState = current._addressState,
                addressPostalCode = current._addressPostalCode,
            )
        } else {
            BusinessModel(
                id = current._id,
                name = current._name,
                email = current._email,
                phoneNumber = current._phoneNumber,
                addressStreet = current._addressStreet,
                addressCity = current._addressCity,
                addressState = current._addressState,
                addressPostalCode = current._addressPostalCode,
            )
        }
    }

    fun addContact() {
        val contact = newContact.value
        Log.d("Added Contact", "$contact")
        viewModelContacts.addOne(contact) // Add contact to the list
        resetContact()
    }

    fun resetContact()
    {
        // Reset the form
        isPerson = true
        isAddressOpen = true
        isFamily = true
        opinionRating = 0
        daysOpen = mutableStateListOf(true, true, true, true, true, true, true)

        // Reset newContact with a fresh contact model
        newContact.value = if (isPerson) {
            PersonModel(viewModelContacts.getUniqueId())
        } else {
            BusinessModel(viewModelContacts.getUniqueId())
        }
    }


}
