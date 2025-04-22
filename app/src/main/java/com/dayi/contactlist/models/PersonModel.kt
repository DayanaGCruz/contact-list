package com.dayi.contactlist.models

import android.location.Location
import android.net.Uri
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalDate
import java.util.Date


class PersonModel (
    id: Int = 0,
    name: String = "",
    email: String = "",
    phoneNumber: String = "",
     addressStreet: String = "",
     addressCity: String = "",
     addressState: String = "",
     addressPostalCode: String = "",
    override var photoUri: String = "",
     private var isFamilyMember: Boolean = false,
    @JsonDeserialize(using = LocalDateDeserializer::class)
     private var birthday: LocalDate = LocalDate.now()
) : ContactModel(id, name, email, phoneNumber, addressStreet, addressCity, addressState, addressPostalCode, photoUri)
{

   override var _photoUri: String
        get() = photoUri
        set(value) { photoUri = value}
    var _isFamilyMember: Boolean
        get() = isFamilyMember
        set(value) {isFamilyMember = value}
    var _birthday: LocalDate
        get() = birthday
        set(value) {
            birthday = value
        }

    override fun toString(): String {
        return super.toString() + "photoUri : $_photoUri\n" +
                "isFamilyMember: $isFamilyMember\n" +
                "birthday: $birthday"
    }

    override fun copy() : PersonModel
    {
        return PersonModel(super._id,super._name,super._email, super._phoneNumber, super._addressStreet, super._addressCity, super._addressState, super._addressPostalCode, photoUri, isFamilyMember, birthday)
    }
}