package com.dayi.contactlist.screens

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.dayi.contactlist.R
import com.dayi.contactlist.models.BusinessModel
import com.dayi.contactlist.models.PersonModel
import com.dayi.contactlist.models.ViewModelAddContact
import com.dayi.contactlist.models.ViewModelContacts
import com.dayi.contactlist.models.ViewModelEditContact
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun EditContact(
    viewModelContacts: ViewModelContacts,
    viewModelEditContact: ViewModelEditContact,
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Edit/Cancel Buttons & Person/Business Switch
        Row(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val switchLabel = if (viewModelEditContact._isPerson) "Person" else "Business"

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = viewModelEditContact._isPerson,
                    onCheckedChange = { viewModelEditContact._isPerson = it }
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = switchLabel)
            }

            Button(
                onClick = {
                    val newContact = viewModelEditContact._newContact.value
                    viewModelContacts.updateOne(newContact)
                    navHostController.navigate("All Contacts") {
                        popUpTo(navHostController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            ) {
                Text("Save", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Scrollable Content identical to AddContact
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                val newContact = viewModelEditContact._newContact.value

                if(newContact.photoUri.isEmpty()) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawCircle(color = Color.Gray)
                    }
                } else {
                    Button(onClick = {
                        viewModelEditContact._newContact.value = newContact.copy().apply {
                            photoUri = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Contact Photo")
                    }
                    Image(
                        painter = rememberAsyncImagePainter(newContact.photoUri),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }

                val context = LocalContext.current
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        uri?.let {
                            context.contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            viewModelEditContact._newContact.value = newContact.copy().apply {
                                photoUri = it.toString()
                            }
                        }
                    }
                )

                var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture(),
                    onResult = { success ->
                        if (success) {
                            cameraPhotoUri?.let {
                                viewModelEditContact._newContact.value = newContact.copy().apply {
                                    photoUri = it.toString()
                                }
                            }
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("Gallery")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val photoFile = createImageFile(context)
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                photoFile
                            )
                            cameraPhotoUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("Take Photo")
                    }
                }

                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                TextField(
                    modifier = Modifier.width(260.dp),
                    value = newContact._name,
                    onValueChange = { newName ->
                        viewModelEditContact._newContact.value = newContact.copy().apply { _name = newName }
                    },
                    singleLine = true,
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                TextField(
                    modifier = Modifier.width(260.dp),
                    value = newContact._email,
                    onValueChange = { newEmail ->
                        viewModelEditContact._newContact.value = newContact.copy().apply { _email = newEmail }
                    },
                    singleLine = true,
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                TextField(
                    modifier = Modifier.width(260.dp),
                    value = newContact._phoneNumber,
                    onValueChange = { newPhone ->
                        viewModelEditContact._newContact.value = newContact.copy().apply { _phoneNumber = newPhone }
                    },
                    singleLine = true,
                    label = { Text("Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                var birthdayText by rememberSaveable { mutableStateOf("") }
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                if (newContact is PersonModel) {
                    TextField(
                        modifier = Modifier.width(260.dp),
                        value = birthdayText,
                        onValueChange = { text ->
                            birthdayText = text
                            try {
                                val parsed = LocalDate.parse(text, dateFormatter)
                                viewModelEditContact._newContact.value = newContact.copy().apply { _birthday = parsed }
                            } catch (_: Exception) {}
                        },
                        label = { Text("Birthday (YYYY-MM-DD)") },
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Family", style = MaterialTheme.typography.labelMedium)
                        Checkbox(
                            checked = viewModelEditContact._isFamily,
                            onCheckedChange = {
                                viewModelEditContact._isFamily = it
                                viewModelEditContact._newContact.value = newContact.copy().apply { _isFamilyMember = it }
                            }
                        )
                    }
                }

                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                Row(
                    modifier = Modifier.clickable { viewModelEditContact._isAddressOpen = !viewModelEditContact._isAddressOpen },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Address", style = MaterialTheme.typography.labelMedium)
                    Icon(
                        painter = if (viewModelEditContact._isAddressOpen)
                            painterResource(R.drawable.arrow_drop_up)
                        else painterResource(R.drawable.arrow_drop_down),
                        contentDescription = "Toggle Open Arrow"
                    )
                }

                if (viewModelEditContact._isAddressOpen) {
                    TextField(
                        modifier = Modifier.width(260.dp), singleLine = true,
                        label = { Text("Street") }, value = newContact._addressStreet,
                        onValueChange = { street -> viewModelEditContact._newContact.value = newContact.copy().apply { _addressStreet = street } }
                    )
                    TextField(
                        modifier = Modifier.width(260.dp), singleLine = true,
                        label = { Text("City") }, value = newContact._addressCity,
                        onValueChange = { city -> viewModelEditContact._newContact.value = newContact.copy().apply { _addressCity = city } }
                    )
                    TextField(
                        modifier = Modifier.width(260.dp), singleLine = true,
                        label = { Text("State") }, value = newContact._addressState,
                        onValueChange = { state -> viewModelEditContact._newContact.value = newContact.copy().apply { _addressState = state } }
                    )
                    TextField(
                        modifier = Modifier.width(260.dp), singleLine = true,
                        label = { Text("Postal Code") }, value = newContact._addressPostalCode,
                        onValueChange = { postal -> viewModelEditContact._newContact.value = newContact.copy().apply { _addressPostalCode = postal } }
                    )
                }

                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                if (newContact is BusinessModel) {
                    Text("Web URL", style = MaterialTheme.typography.labelMedium)
                    TextField(
                        modifier = Modifier.width(260.dp), singleLine = true,
                        label = { Text("Web URL") }, value = newContact._webURL,
                        onValueChange = { web -> viewModelEditContact._newContact.value = newContact.copy().apply { _webURL = web } }
                    )
                    HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)
                    Text("Social Media URL", style = MaterialTheme.typography.labelMedium)
                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)

                    // Facebook
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.icons8_facebook), contentDescription = "Facebook Icon")
                        TextField(
                            modifier = Modifier.width(260.dp), singleLine = true,
                            value = newContact._socialMediaLinks.getOrDefault("Facebook", ""),
                            onValueChange = { fb ->
                                val updated = newContact._socialMediaLinks.toMutableMap()
                                updated["Facebook"] = fb
                                viewModelEditContact._newContact.value = newContact.copy().apply { _socialMediaLinks = updated }
                            }
                        )
                    }
                    // X
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.x), contentDescription = "X Icon")
                        TextField(
                            modifier = Modifier.width(260.dp), singleLine = true,
                            value = newContact._socialMediaLinks.getOrDefault("X", ""),
                            onValueChange = { x ->
                                val updated = newContact._socialMediaLinks.toMutableMap()
                                updated["X"] = x
                                viewModelEditContact._newContact.value = newContact.copy().apply { _socialMediaLinks = updated }
                            }
                        )
                    }

                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                    Text("Opinion Rating", style = MaterialTheme.typography.labelMedium)
                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                    Row {
                        repeat(5) { i ->
                            val index = i + 1
                            val starRes = if (index <= viewModelEditContact._opinionRating) R.drawable.star_filled else R.drawable.star_filled_empty
                            Icon(
                                painter = painterResource(starRes),
                                contentDescription = "Star",
                                modifier = Modifier.clickable { viewModelEditContact._opinionRating = index }
                            )
                        }
                    }

                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                    Text("Days Open", style = MaterialTheme.typography.labelMedium)
                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                    Row {
                        val days = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
                        viewModelEditContact._daysOpen.forEachIndexed { idx, checked ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(days[idx])
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { isChecked -> viewModelEditContact._daysOpen[idx] = isChecked }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun createImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply { parentFile?.mkdirs() }
}
