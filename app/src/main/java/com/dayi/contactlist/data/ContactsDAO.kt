

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.dayi.contactlist.models.BusinessModel
import com.dayi.contactlist.models.ContactModel
import com.dayi.contactlist.models.PersonModel
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.addDeserializer


import java.io.File

 class ContactsDAO(context: Context, file: String) : ViewModel()  {
    val appContext = context
    val fileName = file
    // Define the polymorphic type validator
    val ptv: PolymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
        .allowIfSubType("java.util.ArrayList")
        .allowIfSubType("java.util.LinkedHashMap")
        .allowIfSubType("com.dayi.contactlist.models")
        .build()

    val file = File(appContext.filesDir, fileName)

     val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())  // Register Java 8 date/time module
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)

    fun saveContacts(contactsList: List<ContactModel>)
    {

        // Persist contacts to JSON in device file system
        // Activate default typing for non-final classes
        try {
            objectMapper.writeValue(file, contactsList)

        } catch (e: Exception) {
            Log.e("ContactsDAO", "Failed to save contacts: ${e.message}")
        }

    }

    fun loadContacts(): MutableList<ContactModel> {
        return if (file.exists()) {
            try {
                val contacts = objectMapper.readTree(file)

                val contactList: List<ContactModel> = contacts.map {
                    val type = it[0].asText() // Extract the type information (e.g., PersonModel)
                    val dataNode = it[1]      // Extract the actual contact data

                    when (type) {
                        "com.dayi.contactlist.models.PersonModel" -> objectMapper.treeToValue(dataNode, PersonModel::class.java)
                        "com.dayi.contactlist.models.BusinessModel" -> objectMapper.treeToValue(dataNode, BusinessModel::class.java)
                        else -> throw IllegalArgumentException("Unknown type: $type")
                    }
                }
                contactList.toMutableList()
            } catch (e: Exception) {
                Log.e("ContactsDAO", "Failed to load contacts: ${e.message}")
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }

}