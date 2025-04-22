package com.dayi.contactlist

import ContactsDAO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.dayi.contactlist.models.ViewModelAddContact
import com.dayi.contactlist.models.ViewModelContacts
import com.dayi.contactlist.models.ViewModelEditContact
import com.dayi.contactlist.ui.theme.ContactListTheme
import com.dayi.contactlist.screens.AddContact
import com.dayi.contactlist.models.ViewModelSearchContacts
import com.dayi.contactlist.screens.AllContactsScreen
import com.dayi.contactlist.screens.ContactDetails
import com.dayi.contactlist.screens.EditContact
import com.dayi.contactlist.screens.SearchContacts

// Dayana Gonzalez Cruz
// CST-416: Mobile Game Design
// TR300PM
// April 21, 2025
// These project files are my own work, written with the resources and assignment documents provided as course materials.

// Entry Point
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ContactListTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}


// Set up root composable with screen-dynamic top-bar, bottom bar, and nav graph
@Composable
fun MainScreen() {

    val context = LocalContext.current
    val fileName = "contacts.json"
    // Create ViewModels
    val contactsDAO: ContactsDAO = remember { ContactsDAO(context, fileName)}
    val viewModelContacts: ViewModelContacts = remember { ViewModelContacts(contactsDAO, context) }
    val viewModelAddContact: ViewModelAddContact = remember { ViewModelAddContact(viewModelContacts) }
    val viewModelSearchContacts: ViewModelSearchContacts =  remember { ViewModelSearchContacts(viewModelContacts)}
    // Manages navigation between screens
    val navController = rememberNavController()
    //val viewModelEditContact: ViewModelEditContact = remember { ViewModelEditContact(viewModelContacts, ContactModel(), navController) }
    // Observe back stack entry to current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "All Contacts"

    val viewModelEditContact = remember { mutableStateOf<ViewModelEditContact?>(null) }
    // Layout structure
    Scaffold(
        topBar = { TopNavBar(currentRoute,navController) },  // Top navigation bar
        bottomBar = { BottomNavBar(navController) } // Bottom navigation bar
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            // Handle navigation between composables
            NavigationGraph(navController, viewModelContacts, viewModelAddContact, viewModelSearchContacts,viewModelEditContact)
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, viewModelContacts: ViewModelContacts, viewModelAddContact: ViewModelAddContact, viewModelSearchContacts: ViewModelSearchContacts, viewModelEditContact: MutableState<ViewModelEditContact?>) {
    NavHost(navController = navController, startDestination = "All Contacts") {
        // Define composable destinations
        composable("All Contacts") { AllContactsScreen(viewModelContacts, navController) }
        composable("Add a Contact") { AddContact(viewModelContacts, viewModelAddContact) }
        composable("Search") {
            SearchContacts(
                viewModelContacts,
                viewModelSearchContacts,
                navController
            )
        }
        composable("Details/{contactId}") { navBackStackEntry ->
            val contactId = navBackStackEntry.arguments?.getString("contactId")?.toIntOrNull()
            val contact = contactId?.let { viewModelContacts.getOne(contactId) }
            if (contact != null) {
                ContactDetails(viewModelContacts, contact)
            } else {
                Text("Could not retrieve contact")
            }
        }
        composable("Edit/{contactId}") { backStack ->
            val contactId = backStack.arguments?.getString("contactId")?.toIntOrNull()
            val contact = contactId?.let { viewModelContacts.getOne(it) }

            if (contact != null) {
                // Only initialize if null or if contact changed
                if (viewModelEditContact.value == null ||
                    viewModelEditContact.value?._newContact?.value?._id != contact._id) {
                    viewModelEditContact.value = ViewModelEditContact(
                        viewModelContacts,
                        contact,
                        navController
                    )
                }

                viewModelEditContact.value?.let { vm ->
                    EditContact(
                        viewModelContacts = viewModelContacts,
                        viewModelEditContact = vm,
                        navHostController = navController
                    )
                }
            } else {
                Text("Could not retrieve contact")
            }
        }
    }
}
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun TopNavBar(currentRoute: String, navController: NavHostController) {
            TopAppBar(
                title = { Text(getScreenTitle(currentRoute)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    // Add a return to contact list screen button if on add contact, search, or detail screens
                    if (currentRoute == "Add a Contact" || currentRoute == "Search" || currentRoute.startsWith("Details")|| currentRoute.startsWith("Edit")
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.keyboard_return),
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }


// Returns top bar header title for each screen

        fun getScreenTitle(route: String): String {
            return when {
                route == "All Contacts" -> "Your Contacts"
                route == "Add a Contact" -> "Add a New Contact"
                route == "Search" -> "Search Contacts"
                route.startsWith("Details") -> "Contact Details"
                route.startsWith("Edit") -> "Edit Contact"
                else -> "Contacts App"
            }
        }


        @Composable
        fun BottomNavBar(navController: NavHostController) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination


            val screens = listOf(
                ScreenInfo(
                    "All Contacts",
                    R.drawable.list,
                    "List",
                    {
                        AllContactsScreen(
                            viewModelContacts = viewModel(),
                            navHostController = navController
                        )
                    }),
                ScreenInfo(
                    "Add a Contact",
                    R.drawable.user_plus,
                    "Add",
                    {
                        AddContact(
                            viewModelContacts = viewModel(),
                            viewModelAddContact = viewModel()
                        )
                    }),
                ScreenInfo(
                    "Search",
                    R.drawable.search,
                    "Search",
                    {
                        SearchContacts(
                            viewModelContacts = viewModel(),
                            viewModelSearchContacts = viewModel(),
                            navController
                        )
                    }),
            )

            NavigationBar {
                // Iterate over the list of screens and create a NavigationBarItem for each
                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.label) },
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.iconRes),
                                contentDescription = screen.routeName
                            )
                        },
                        selected = currentDestination?.route == screen.routeName,
                        onClick = {
                            // Navigate to the selected screen
                            navController.navigate(screen.routeName) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }



// Utility class holds screen info
data class ScreenInfo(
    val routeName: String,
    val iconRes: Int, // Resource ID of the custom SVG icon
    val label: String,
    val composable: @Composable () -> Unit
)



