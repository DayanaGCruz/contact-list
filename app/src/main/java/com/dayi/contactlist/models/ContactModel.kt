package com.dayi.contactlist.models

import android.net.Uri
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


open class ContactModel(
    @JsonProperty("_id") private var id : Int = 0,
    private var name: String = "",
    private var email: String = "",
    private var phoneNumber: String = "",
    private var addressStreet: String = "",
    private var addressCity: String = "",
    private var addressState: String = "",
    private var addressPostalCode: String = "",
    open var photoUri: String = "",
    private var isFavorite: Boolean = true,
)
{
    // No-arg constructor for Jackson deserialization
    constructor() : this(
        id = 0,
        name = "",
        email = "",
        phoneNumber = "",
        addressStreet = "",
        addressCity = "",
        addressState = "",
        addressPostalCode = "",
        photoUri ="",
        //isFavorite = false,
    )


    @get:JsonProperty("_id")
    var _id : Int get() = id
        set(value) {id = value}
    var _name: String
        get() = name
        set(value) { name = value}
    var _email: String
        get() = email
        set(value) { email = value}
    var _phoneNumber: String
        get() = phoneNumber
        set(value) { phoneNumber = value}
    var _addressStreet: String
        get() = addressStreet
        set(value) {addressStreet = value}
    var _addressCity: String
        get() = addressCity
        set(value) { addressCity = value }
    var _addressState: String
        get() = addressState
        set(value) {addressState = value}
    var _addressPostalCode: String
        get() = addressPostalCode
        set(value) { addressPostalCode = value}
   open var _photoUri: String
            get() = photoUri
            set(value) { photoUri = value}

    val _address: String @JsonIgnore
    get() = "$addressStreet, $addressCity, $addressState, $addressPostalCode"

    var _isFavorite: Boolean
        get() = isFavorite
        set(value) {isFavorite = value}

    override fun toString(): String
    {
        return "id: $id\n" +
                "name: $name\n" +
                "email: $email\n" +
                "phoneNumber: $phoneNumber\n" +
                "addressStreet: $addressStreet\n" +
                "addressCity: $addressCity\n" +
                "addressState: $addressState\n" +
                "addressPostalCode: $addressPostalCode\n" +
                "photoUri: $photoUri\n" +
                "isFavorite: $isFavorite\n"
    }

    open fun copy() : ContactModel {
        return ContactModel(id, name, email, phoneNumber, addressStreet, addressCity, addressState, addressPostalCode, photoUri, isFavorite)
    }

}