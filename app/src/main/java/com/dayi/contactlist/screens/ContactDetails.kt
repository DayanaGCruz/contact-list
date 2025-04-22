package com.dayi.contactlist.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.dayi.contactlist.R
import com.dayi.contactlist.models.BusinessModel
import com.dayi.contactlist.models.ContactModel
import com.dayi.contactlist.models.PersonModel
import com.dayi.contactlist.models.ViewModelContacts
import com.dayi.contactlist.ui.theme.Mimosa
import java.time.LocalDate

@Composable
fun ContactDetails(viewModelContacts: ViewModelContacts, contact: ContactModel)
{

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(contact.photoUri.isEmpty()) {// Circle icon placeholder for profile image
                Canvas(modifier = Modifier.size(120.dp)) {
                    drawCircle(color = Color.Gray)
                }
            } else
            {
            Image(
                painter = rememberAsyncImagePainter(contact.photoUri),
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )}

            Spacer(Modifier.height(10.dp))


            Row(verticalAlignment = Alignment.CenterVertically,) {
                if (contact is PersonModel && contact._isFamilyMember) {
                    Text("Family", style = MaterialTheme.typography.labelSmall)
                    Icon(
                        painter = painterResource(R.drawable.mood),
                        contentDescription = "Family Icon"
                    )
                }
                if (contact is PersonModel && contact._birthday.isEqual(LocalDate.now())) {
                    Text("Birthday!", style = MaterialTheme.typography.labelSmall)
                    Icon(
                        painter = painterResource(R.drawable.gift),
                        contentDescription = "Birthday"
                    )
                }
            }

            Text(
                text = contact._name,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(10.dp))

            // Bar containing Actions (Call, Message, Email, Locate)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = MaterialTheme.colorScheme.secondary),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FavoriteButton(contact, viewModelContacts)
                // Action Button Composables
                CallButton(viewModelContacts.appContext, contact._phoneNumber)
                MessageButton(viewModelContacts.appContext, contact._phoneNumber)
                EmailButton(viewModelContacts.appContext, contact._email)
                LocateButton(viewModelContacts.appContext, contact._address)

            }
        }


        // Contact Information

        Column (modifier = Modifier.padding(15.dp)) {
            Text(text = "Phone", style = MaterialTheme.typography.labelMedium)
            Text(contact._phoneNumber, style = MaterialTheme.typography.bodySmall)
            Text(text = "Email", style = MaterialTheme.typography.labelMedium)
            Text(contact._email, style = MaterialTheme.typography.bodySmall)
            Text(text = "Address", style = MaterialTheme.typography.labelMedium)
            Text(text = contact._address, style = MaterialTheme.typography.bodySmall)
            Text(text = "Picture", style = MaterialTheme.typography.labelMedium)
            Text(text = contact.photoUri, style = MaterialTheme.typography.bodySmall)
            // Information specific to sub-class
            when (contact) {
                is BusinessModel -> {
                    Text(text = "Opinion Rating", style = MaterialTheme.typography.labelMedium)
                    Row() {
                        for (i in 1..5) {
                            val starIcon =
                                if (i <= contact._myOpinionRating) R.drawable.star_filled else R.drawable.star_filled_empty
                            Icon(
                                painter = painterResource(starIcon),
                                contentDescription = "Star Icon",
                                tint = if (i <= contact._myOpinionRating)
                                    Color.Unspecified else Color.Black
                            )
                        }
                    }
                    Text(text = "Website", style = MaterialTheme.typography.labelMedium)
                    Text("${contact._webURL}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Social Media", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            tint = Color.Unspecified,
                            painter = painterResource(R.drawable.icons8_facebook),
                            contentDescription = "Facebook Icon"
                        )
                        Text(
                            contact._socialMediaLinks.getValue("Facebook"),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(painter = painterResource(R.drawable.x), contentDescription = "X Icon")
                        Text(
                            contact._socialMediaLinks.getValue("X"),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    //Days Open
                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                    Text("Days Open", style = MaterialTheme.typography.labelMedium)
                    HorizontalDivider(thickness = 10.dp, color = Color.Unspecified)
                    Row {
                        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thurs", "Fri", "Sat")
                        contact._daysOpen.forEachIndexed { index, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally)
                            {
                                Text(days.get(index))
                                Checkbox(
                                    checked = day,
                                    onCheckedChange = { isChecked ->
                                        contact._daysOpen[index] = isChecked
                                    }
                                )
                            }
                        }
                    }
                }

                is PersonModel -> {
                    Text(text = "Birthday", style = MaterialTheme.typography.labelMedium)
                    Text("${contact._birthday}", style = MaterialTheme.typography.bodySmall)

                }
            }

        }
    }

}

// Opens default phone dialer with given number
@Composable
fun CallButton(context: Context, phoneNumber: String)
{
    Button(
        onClick =
        {
            val callIntent: Intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }

            try{
            context.startActivity(callIntent)}
            catch(e:Exception)
            {
                Log.e("Contact Details Action", "Exception:$e")
            }
        }
    )
    {
    Icon(painter = painterResource(R.drawable.phone), contentDescription = "Call")
    }
}

// Opens email draft in default email app with given string as recipient
@Composable
fun EmailButton(context: Context, emailAddress: String)
{
    Button(
        onClick =
        {
            val mailIntent: Intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$emailAddress")
            }

            try {
            context.startActivity(mailIntent)}
            catch(e: Exception)
            {
                Log.e("Contact Details Action", "Exception:$e")
            }
        }
    )
    {
        Icon(painter = painterResource(R.drawable.mail), contentDescription = "Email")
    }
}

// Opens default messaging app with given phone number as recipient
@Composable
fun MessageButton(context: Context, phoneNumber: String)
{
    Button(
        onClick = {
            val messageIntent: Intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
            }
            try {
                context.startActivity(messageIntent)}
            catch(e: Exception)
            {
                Log.e("Contact Details Action", "Exception:$e")
            }
        }
    ) {
        Icon(painter = painterResource(R.drawable.chat_bubble), contentDescription = "Message")
    }
}

// Opens default map application with given location
@Composable
fun LocateButton(context: Context, address: String)
{
    Button(
        onClick = {
            val locateIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            }

            try {
            context.startActivity(locateIntent)}
            catch(e: Exception)
            {
                Log.e("Contact Details Action", "Exception:$e")
            }
        }
    )
    {
        Icon(painter = painterResource(R.drawable.map_pin), contentDescription = "Locate")
    }

}

@Composable
fun FavoriteButton(contact: ContactModel, viewModelContacts: ViewModelContacts) {
    val isFavorite = remember { mutableStateOf(contact._isFavorite) }
    Log.e("FavButton", "$contact")
    Icon(
        modifier = Modifier.clickable {
            // Toggle and update both UI and model
            isFavorite.value = !isFavorite.value
            contact._isFavorite = isFavorite.value
            viewModelContacts.updateOne(contact)
        },
        painter = painterResource(
            if (isFavorite.value) R.drawable.star_filled else R.drawable.star_filled_empty
        ),
        contentDescription = "Favorite",
        tint = Color.Unspecified
    )
}

