package com.dayi.contactlist.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dayi.contactlist.R
import com.dayi.contactlist.models.ViewModelContacts
import com.dayi.contactlist.models.ViewModelSearchContacts

@Composable
fun SearchContacts(viewModelContacts: ViewModelContacts, viewModelSearchContacts: ViewModelSearchContacts, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Search text field
        TextField(
            placeholder = { Text("Search by name") },
            modifier = Modifier.fillMaxWidth(),
            value = viewModelSearchContacts._searchFieldState.value,
            onValueChange = { newValue: String ->
                // Update the search field state as the user types
                viewModelSearchContacts._searchFieldState.value = newValue
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            trailingIcon = {
                IconButton(onClick = {
                    // Perform the search when the icon is clicked

                    if(viewModelSearchContacts.searchFilters.any {it == true} || viewModelSearchContacts.personFilters.any { it == true} || viewModelSearchContacts.businessFilters.any{it == true})
                    {
                        viewModelContacts.filteredSearchByName(viewModelSearchContacts._searchFieldState.value, viewModelSearchContacts.searchFilters, viewModelSearchContacts.personFilters, viewModelSearchContacts.businessFilters)
                    }
                    else
                    {
                        viewModelContacts.searchByName(viewModelSearchContacts._searchFieldState.value)
                    }
                    viewModelContacts._contactsSearchResults.forEach {
                        Log.d(
                            "Search Result",
                            "$it"
                        )
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search Icon"
                    )
                }
            }
        )



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(10.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModelSearchContacts.filterTabOpen.value = !viewModelSearchContacts.filterTabOpen.value },
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = "Filters", style = MaterialTheme.typography.labelMedium)
                val icon = if (viewModelSearchContacts.filterTabOpen.value) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down
                Icon(painter = painterResource(icon), contentDescription = "Expand or Collapse")
            }



            if (viewModelSearchContacts.filterTabOpen.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) { FilterCheckbox("General","Favorites", viewModelSearchContacts.searchFilters, 0, viewModelSearchContacts) }

                Text(text = "Person", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) { FilterCheckbox("Person","Family", viewModelSearchContacts.personFilters, 0, viewModelSearchContacts)}
                Text(text = "Business", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {  FilterCheckbox("Business",">3 Stars", viewModelSearchContacts.businessFilters, 0, viewModelSearchContacts)}


            }
            else
            {
                if(viewModelSearchContacts.searchFilters.any {it == true} || viewModelSearchContacts.personFilters.any { it == true} || viewModelSearchContacts.businessFilters.any{it == true})
                {
                    Text("Some filters selected.")
                }
                else
                {
                    Text("No filters selected.")
                }
            }
        }

        SearchResultContactList(viewModelContacts, navHostController)
    }
}
    @Composable
    fun FilterCheckbox(type: String, label: String, filters: MutableList<Boolean>, index: Int, viewModelSearchContacts: ViewModelSearchContacts) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = filters[index],
                onCheckedChange = {
                    if(type == "Business")
                    {
                        viewModelSearchContacts.personFilters.indices.forEach {i -> viewModelSearchContacts.personFilters[i] = false}
                    }
                    if(type == "Person")
                    {
                        viewModelSearchContacts.businessFilters.indices.forEach { i -> viewModelSearchContacts.businessFilters[i] = false}
                    }

                    filters[index] = it
                }
            )
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }



