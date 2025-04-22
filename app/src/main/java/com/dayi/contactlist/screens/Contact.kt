package com.dayi.contactlist.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.dayi.contactlist.R
import com.dayi.contactlist.models.BusinessModel
import com.dayi.contactlist.models.ContactModel
import com.dayi.contactlist.models.PersonModel
import com.dayi.contactlist.ui.theme.Gray
import com.fasterxml.jackson.core.util.RequestPayload
import java.time.LocalDate


@Composable
fun Contact(
    contact : ContactModel,
   onClick : (ContactModel) -> Unit)
{
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .clickable { onClick(contact) },

        ) {
        Column (
            modifier = Modifier.padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top= 12.dp,bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {


                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                )
                {

                    if(contact.photoUri.isEmpty()) {
                        Canvas(modifier = Modifier.size(60.dp)) {
                            drawCircle(color = Gray)
                        }
                    } else
                    {
                        Log.e("hi", "${contact.photoUri}")
                        Image(
                            painter = rememberAsyncImagePainter(contact.photoUri),
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.size(14.dp))
                    Text(
                        text = contact._name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        if (contact is PersonModel) {
                            if (contact._isFamilyMember) {
                                Text("Family", style = MaterialTheme.typography.labelSmall)
                                Icon(
                                    painter = painterResource(R.drawable.mood),
                                    contentDescription = "Family Icon")
                            }
                            if (contact._birthday.month == LocalDate.now().month && contact._birthday.dayOfMonth == LocalDate.now().dayOfMonth) {
                                Text("Birthday!", style = MaterialTheme.typography.labelSmall)
                                Icon(
                                    painter = painterResource(R.drawable.gift),
                                    contentDescription = "Birthday"
                                )
                            }
                    }
                }
            }

        }
    }
}

