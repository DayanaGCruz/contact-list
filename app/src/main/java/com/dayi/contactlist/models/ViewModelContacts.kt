package com.dayi.contactlist.models

import ContactsDAO
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class ViewModelContacts (
    myDAO: ContactsDAO,
    context: Context
) : ViewModel() {
    val appContext = context
    val contactsDAO = myDAO
    // Private mutable lists
    private var _contactsList = mutableStateListOf<ContactModel>()

    // Public Lists
    var contactsList: List<ContactModel>
        get() = _contactsList
        set(value) {
            _contactsList = value.toMutableStateList()
        }
    private var contactsSearchResults = mutableStateListOf<ContactModel>()
    val _contactsSearchResults: List<ContactModel>
        get() = contactsSearchResults

    init {
        if(contactsList.isEmpty())
        {
            loadAll() // Load contacts from JSON if not having already done so
        }
    }
    // Add a contact
    fun addOne(contact: ContactModel)
    {
        Log.e("ViewModelContacts", "Added Contact")
        _contactsList.add(contact)
        saveAll()
    }

    // Delete a contact by id

    fun deleteOne(id: Int) : Int?
    {
        val contactToRemove = _contactsList.find {it._id == id}
        if(contactToRemove != null)
        {
            _contactsList.remove(contactToRemove)
            saveAll()
            return id;
        }
        else {return null}
    }

    // Get a contact by id
    fun getOne(id: Int): ContactModel?
    {
        Log.d("ViewModelContacts", "Getting contact ID")
        return _contactsList.find { it._id == id }
    }

    fun searchByName(query: String) {
        // Clear existing results and add the filtered ones
        contactsSearchResults.clear()
        contactsSearchResults.addAll(_contactsList.filter { it._name.contains(query, ignoreCase = true) })
    }

    fun filteredSearchByName(query: String, generalFilters: List<Boolean>, personFilters: List<Boolean>, businessFilters: List<Boolean>)
    {
        contactsSearchResults.clear()
        // Clear existing results and add the filtered ones
        var filteredContacts = _contactsList.filter { contact ->
        contact._name.contains(query, ignoreCase = true) }
        filteredContacts = filterGeneral(filteredContacts, searchFilters = generalFilters)

        if (personFilters.any { it }) {
            filteredContacts = filteredContacts.mapNotNull { contact ->
                if (contact is PersonModel) {
                    filterPersonContacts(listOf(contact), personFilters).firstOrNull()
                } else null
            }.map { it as ContactModel } // Cast to common type
        } else  if(businessFilters.any{it}){
            filteredContacts = filteredContacts.mapNotNull { contact ->
                if (contact is BusinessModel) {
                    filterBusinessContacts(listOf(contact), businessFilters).firstOrNull()
                } else null
            }.map { it as ContactModel }
        }
        contactsSearchResults.addAll(filteredContacts)
    }

    fun filterGeneral(
        contacts: List<ContactModel>,
        searchFilters: List<Boolean>) : List<ContactModel>
    {
        var filteredContacts = contacts

        if(searchFilters.getOrElse(0) {false})
        {
            filteredContacts = filteredContacts.filter {it._isFavorite}
        }
        filteredContacts.forEach { Log.e("ContactList", "${it._name} favorite=${it._isFavorite}") }
        return filteredContacts
    }

    fun filterPersonContacts(
        personContacts: List<PersonModel>,
        personFilters: List<Boolean>
    ): List<PersonModel> {
        var filteredContacts = personContacts
        // Only apply family filter if the first filter is true
        if (personFilters.getOrElse(0) { false }) {
            filteredContacts = filteredContacts.filter { it._isFamilyMember }
        }

        return filteredContacts
    }

    fun filterBusinessContacts(businessContacts : List<BusinessModel>, businessFilters: List<Boolean>) : List<BusinessModel>
    {
        var filteredContacts : List<BusinessModel> = businessContacts

        if (businessFilters[0] == true)
        {
            filteredContacts = businessContacts.filter { it._myOpinionRating >= 3 }
        }
        return filteredContacts
    }

    fun updateOne(contact: ContactModel) : ContactModel? {
        val contactToUpdate = getOne(contact._id)
        if (contactToUpdate != null) {
            val index = _contactsList.indexOf(contactToUpdate)
            _contactsList[index] = contact

            // Also update in the search results
            val searchIndex = contactsSearchResults.indexOf(contactToUpdate)
            if (searchIndex != -1) {
                contactsSearchResults[searchIndex] = contact
            }

            saveAll()
            return _contactsList[index]
        }
        return null
    }


    // Get a  unique id not present in the contactList already
    fun getUniqueId(): Int
    {
        return if (_contactsList.isEmpty()) 1 else _contactsList.maxOf { it._id } + 1
    }


    fun saveAll(){
        contactsDAO.saveContacts(contactsList) // Save contacts to JSON
    }
    fun loadAll(){
        _contactsList.clear()
        _contactsList = contactsDAO.loadContacts().toMutableStateList() // Retrieve contacts from JSON
        Log.e("Loaded Contacts", "$_contactsList")
    }

}
