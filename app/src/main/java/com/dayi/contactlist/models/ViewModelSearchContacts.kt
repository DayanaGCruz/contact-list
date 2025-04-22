package com.dayi.contactlist.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.dayi.contactlist.models.ViewModelContacts

class ViewModelSearchContacts(viewModelContacts: ViewModelContacts) : ViewModel() {
    private var searchFieldState = mutableStateOf("")
    val _searchFieldState get() = searchFieldState

    var filterTabOpen = mutableStateOf(false)
    var searchFilters = mutableStateListOf(false)
    var personFilters = mutableStateListOf(false, false)
    var businessFilters = mutableStateListOf(false, false)

}