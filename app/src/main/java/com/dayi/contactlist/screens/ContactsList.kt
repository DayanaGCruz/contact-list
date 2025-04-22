package com.dayi.contactlist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dayi.contactlist.models.ViewModelContacts
import com.dayi.contactlist.ui.theme.MilanoRed
import com.dayi.contactlist.ui.theme.SilverTree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactsList(
    viewModelContacts: ViewModelContacts,
    navHostController: NavHostController
) {
    val contacts = viewModelContacts.contactsList
    val scope    = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = contacts, key = { it._id }) { contact ->
            val dismissState = rememberDismissState { value ->
                when (value) {
                    // RIGHT swipe: delete
                    DismissValue.DismissedToEnd -> {
                        scope.launch {
                            delay(300)
                            viewModelContacts.deleteOne(contact._id)
                        }
                        true
                    }
                    // LEFT swipe: navigate to edit, then snap back
                    DismissValue.DismissedToStart -> {
                        scope.launch {
                            navHostController.navigate("Edit/${contact._id}")
                        }
                        false
                    }
                    else -> false
                }
            }

            SwipeToDismiss(
                state             = dismissState,
                directions        = setOf(
                    DismissDirection.StartToEnd,
                    DismissDirection.EndToStart
                ),
                dismissThresholds = { FractionalThreshold(0.5f) },
                background        = {
                    // Show Delete on right‑swipe, Edit on left‑swipe
                    val dir   = dismissState.dismissDirection
                    val color = when (dir) {
                        DismissDirection.StartToEnd -> MilanoRed
                        DismissDirection.EndToStart -> SilverTree
                        null                         -> Color.Transparent
                    }
                    val text  = when (dir) {
                        DismissDirection.StartToEnd -> "Delete"
                        DismissDirection.EndToStart -> "Edit"
                        null                         -> ""
                    }
                    val alignment = when (dir) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                        null                         -> Alignment.CenterStart
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        if (text.isNotEmpty()) {
                            Text(text, color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                },
                dismissContent    = {
                    Surface {
                        Contact(contact = contact) {
                            navHostController.navigate("Details/${contact._id}")
                        }
                    }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultContactList(viewModelContacts: ViewModelContacts, navHostController: NavHostController)
{

    val contacts = viewModelContacts._contactsSearchResults
    val scope    = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = contacts, key = { it._id }) { contact ->
            val dismissState = rememberDismissState { value ->
                when (value) {
                    // RIGHT swipe: delete
                    DismissValue.DismissedToEnd -> {
                        scope.launch {
                            delay(300)
                            viewModelContacts.deleteOne(contact._id)
                        }
                        true
                    }
                    // LEFT swipe: navigate to edit, then snap back
                    DismissValue.DismissedToStart -> {
                        scope.launch {
                            navHostController.navigate("Edit/${contact._id}")
                        }
                        false
                    }
                    else -> false
                }
            }

            SwipeToDismiss(
                state             = dismissState,
                directions        = setOf(
                    DismissDirection.StartToEnd,
                    DismissDirection.EndToStart
                ),
                dismissThresholds = { FractionalThreshold(0.5f) },
                background        = {
                    // Show Delete on right‑swipe, Edit on left‑swipe
                    val dir   = dismissState.dismissDirection
                    val color = when (dir) {
                        DismissDirection.StartToEnd -> MilanoRed
                        DismissDirection.EndToStart -> SilverTree
                        null                         -> Color.Transparent
                    }
                    val text  = when (dir) {
                        DismissDirection.StartToEnd -> "Delete"
                        DismissDirection.EndToStart -> "Edit"
                        null                         -> ""
                    }
                    val alignment = when (dir) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                        null                         -> Alignment.CenterStart
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        if (text.isNotEmpty()) {
                            Text(text, color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                },
                dismissContent    = {
                    Surface {
                        Contact(contact = contact) {
                            navHostController.navigate("Details/${contact._id}")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AllContactsScreen(viewModelContacts: ViewModelContacts, navHostController: NavHostController)
{
    Column(modifier = Modifier.fillMaxSize()) {
        ContactsList(viewModelContacts, navHostController)
    }
}

