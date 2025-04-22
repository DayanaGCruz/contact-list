package com.dayi.contactlist.screens

import android.app.Person
import android.content.Context
import android.content.Intent
import android.graphics.Paint.Align
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.dayi.contactlist.R
import com.dayi.contactlist.models.BusinessModel
import com.dayi.contactlist.models.PersonModel
import com.dayi.contactlist.models.ViewModelAddContact
import com.dayi.contactlist.models.ViewModelContacts
import com.dayi.contactlist.ui.theme.Gray
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.sin
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.draw.clip
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.rememberImagePainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddContact(viewModelContacts: ViewModelContacts, viewModelAddContact: ViewModelAddContact) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add/Cancel Buttons & Person/Business Switch
        Row(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = { viewModelAddContact.resetContact() },
                colors = ButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.Unspecified
                )
            ) {
                Text("Cancel", style = MaterialTheme.typography.labelSmall)
            }


            val switchLabel = if (viewModelAddContact._isPerson) "Person" else "Business"

            Row (verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = viewModelAddContact._isPerson,
                    onCheckedChange = { viewModelAddContact._isPerson = it }
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = switchLabel)
            }

            Button(
                onClick = {
                    viewModelAddContact.addContact()
                },
                colors = ButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.Unspecified
                )

            ) {
                Text("Add", style = MaterialTheme.typography.labelSmall)
            }
        }


        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                val newContact = viewModelAddContact._newContact.value

                if(newContact.photoUri.isEmpty()) {
                    // Circle icon placeholder
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawCircle(color = Color.Gray)
                    }
                }
                else
                {
                    Log.e("AddContact", "${newContact.photoUri}")
                    // Display the selected image with circle clipping
                    Button(onClick = {
                        viewModelAddContact._newContact.value = newContact.copy().apply {
                        photoUri = ""
                    }}
                    ) {
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

                // Gallery launcher
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        uri?.let {
                            viewModelContacts.appContext.contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            viewModelAddContact._newContact.value = newContact.copy().apply {
                                photoUri = it.toString()
                            }
                        }
                    }
                )

                // Camera launcher
                var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture(),
                    onResult = { success ->
                        if (success) {
                            cameraPhotoUri?.let {
                                viewModelAddContact._newContact.value = newContact.copy().apply {
                                    photoUri = it.toString()
                                }
                            }
                        }
                    }
                )

                // Photo selection buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {

                            try {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } catch(e:Exception)
                            {
                                Log.e("AddContact" , "$e")
                            }
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("Gallery")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val context = viewModelContacts.appContext
                            // Create temp file for camera photo
                            val photoFile = createImageFile(context)
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                photoFile
                            )
                            cameraPhotoUri = uri
                            try {
                                cameraLauncher.launch(uri)
                            } catch(e: Exception)
                            {
                                Log.e("AddContact TakePhoto", "$e")
                            }
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("Take Photo")
                    }
                }



                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                // Name Text Field
                TextField(
                    modifier = Modifier
                        .width(260.dp),
                    value = newContact._name,  // Correctly accessing the name property
                    onValueChange = { newName ->
                        viewModelAddContact._newContact.value = newContact.copy().apply {
                            _name = newName  // Create a new object and update its name
                        }
                    },
                    singleLine = true,
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                // Email Text Field
                TextField(
                    modifier = Modifier
                        .width(260.dp),
                    value = newContact._email,  // Accessing the name property
                    onValueChange = { newEmail ->
                        viewModelAddContact._newContact.value = newContact.copy().apply {
                            _email = newEmail
                        }
                    },
                    singleLine = true,
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Phone Text Field
                TextField(
                    modifier = Modifier
                        .width(260.dp),
                    value = newContact._phoneNumber,  // Accessing the name property
                    onValueChange = { newPhoneNumber ->
                        viewModelAddContact._newContact.value = newContact.copy().apply {
                            _phoneNumber = newPhoneNumber
                        }
                    },
                    singleLine = true,
                    label = { Text("Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                var birthdayText by rememberSaveable { mutableStateOf("") }
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                if (newContact is PersonModel) {
                    // Birthday Input Field
                    TextField(
                        modifier = Modifier
                            .width(260.dp),
                        value = birthdayText,
                        onValueChange = { newText ->
                            birthdayText = newText
                            try {
                                val parsedDate = LocalDate.parse(newText, dateFormatter)
                                viewModelAddContact._newContact.value = newContact.copy().apply {
                                    _birthday = parsedDate
                                }
                            } catch (e: Exception) {
                                // TO DO
                            }
                        },
                        label = { Text("Birthday (YYYY-MM-DD)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Family", style = MaterialTheme.typography.labelMedium)
                        Checkbox(
                            checked = viewModelAddContact._isFamily,
                            onCheckedChange = {
                            viewModelAddContact._isFamily = it
                                newContact._isFamilyMember = it
                            }
                        )
                    }
                }

                HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)


                // Text Input for Address
                Row(
                    modifier = Modifier
                        .clickable { viewModelAddContact._isAddressOpen = !viewModelAddContact._isAddressOpen },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Address", style = MaterialTheme.typography.labelMedium)
                    Icon(
                        painter = if (viewModelAddContact._isAddressOpen) painterResource(R.drawable.arrow_drop_up) else painterResource(R.drawable.arrow_drop_down),
                        contentDescription = "Toggle Open Arrow"
                    )
                }

                if(viewModelAddContact._isAddressOpen) {
                    // Street Address
                    TextField(
                        modifier = Modifier
                            .width(260.dp),
                        singleLine = true,
                        label = { Text("Street") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        value = newContact._addressStreet,
                        onValueChange = { newStreetAddr: String ->
                            viewModelAddContact._newContact.value = newContact.copy().apply {
                                _addressStreet = newStreetAddr
                            }
                        },
                    )

                    // City Address
                    TextField(
                        modifier = Modifier
                            .width(260.dp),
                        singleLine = true,
                        label = { Text("City") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        value = newContact._addressCity,
                        onValueChange = { newCityAddr: String ->
                            viewModelAddContact._newContact.value = newContact.copy().apply {
                                _addressCity = newCityAddr
                            }
                        },
                    )

                    // State Address
                    TextField(
                        modifier = Modifier
                            .width(260.dp),
                        singleLine = true,
                        label = { Text("State") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        value = newContact._addressState,
                        onValueChange = { newStateAddr: String ->
                            viewModelAddContact._newContact.value = newContact.copy().apply {
                                _addressState = newStateAddr
                            }
                        },
                    )

                    // Postal Code Address
                    TextField(
                        label = { Text("Postal Code") },
                        modifier = Modifier
                            .width(260.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = newContact._addressPostalCode,
                        onValueChange = { newPostalAddr: String ->
                            viewModelAddContact._newContact.value = newContact.copy().apply {
                                _addressPostalCode = newPostalAddr
                            }
                        },
                    )
                }
                    HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)

                    when (newContact)
                    {

                        is BusinessModel -> {
                            // Web URL
                            Text("Web URL", style = MaterialTheme.typography.labelMedium)
                            TextField(
                                modifier = Modifier
                                    .width(260.dp),
                                singleLine = true,
                                label = {Text("Web URL")},
                                keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Text),
                                value = newContact._webURL,
                                onValueChange = { newWebURL : String ->
                                    viewModelAddContact._newContact.value = newContact.copy().apply {
                                        _webURL = newWebURL
                                    }
                                },
                            )
                            HorizontalDivider(thickness = 30.dp, color = Color.Unspecified)
                            // Social Media URLs
                            Text("Social Media URL", style = MaterialTheme.typography.labelMedium)
                            HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                // Icon on the far left
                                Icon(
                                    tint = Color.Unspecified,
                                    contentDescription = "Facebook Icon",
                                    painter = painterResource(R.drawable.icons8_facebook)
                                )

                                // TextField in the center of the row
                                TextField(
                                    modifier = Modifier
                                        .width(260.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    value = newContact._socialMediaLinks.getOrDefault("Facebook", ""),
                                    onValueChange = { newFBURL: String ->
                                        val updatedLinks = newContact._socialMediaLinks.toMutableMap()
                                        updatedLinks["Facebook"] = newFBURL
                                        viewModelAddContact._newContact.value = newContact.copy().apply {
                                            _socialMediaLinks = updatedLinks
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.width(45.dp)) // Line up with other textfields
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Icon on the far left
                                Icon(
                                    tint = Color.Unspecified,
                                    contentDescription = "X Icon",
                                    painter = painterResource(R.drawable.x)
                                )

                                // TextField in the center of the row
                                TextField(
                                    modifier = Modifier
                                        .width(260.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    value = newContact._socialMediaLinks.getOrDefault("X", ""),
                                    onValueChange = { newXURL: String ->
                                        val updatedLinks = newContact._socialMediaLinks.toMutableMap()
                                        updatedLinks["X"] = newXURL
                                        viewModelAddContact._newContact.value = newContact.copy().apply {
                                            _socialMediaLinks = updatedLinks
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.width(45.dp)) // Line up with other textfields
                            }

                            //Opinion Rating
                            HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                            Text("Opinion Rating", style = MaterialTheme.typography.labelMedium)
                            HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)

                            Row(){
                                for(i in 1..5)
                                {
                                    val starIcon = if(i <= viewModelAddContact._opinionRating)  R.drawable.star_filled else R.drawable.star_filled_empty
                                    Icon(
                                        painter = painterResource(starIcon),
                                        modifier = Modifier.clickable {
                                            viewModelAddContact._opinionRating  = i
                                            newContact.apply { _myOpinionRating = i}},
                                        contentDescription = "Star Icon",
                                        tint = if(i <= viewModelAddContact._opinionRating)
                                        Color.Unspecified else Color.Black)

                                }
                            }

                            //Days Open
                            HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                            Text("Days Open", style = MaterialTheme.typography.labelMedium)
                            HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                            Row {
                                val days = listOf("Sun", "Mon", "Tue", "Wed", "Thurs", "Fri", "Sat")
                                viewModelAddContact._daysOpen.forEachIndexed { index, day ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally)
                                    {
                                        Text(days.get(index))
                                        Checkbox(
                                            checked = day,
                                            onCheckedChange = { isChecked ->
                                                viewModelAddContact._daysOpen[index] = isChecked
                                                newContact.apply {
                                                    _daysOpen[index] = isChecked
                                                }
                                                Log.d("Days Open", "$newContact")
                                            }
                                        )
                                    }
                                }
                            }


                        }
                    }
                }
            }
        }
    }

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    ).apply {
        // Create the directory if it doesn't exist
        parentFile?.mkdirs()
    }
}