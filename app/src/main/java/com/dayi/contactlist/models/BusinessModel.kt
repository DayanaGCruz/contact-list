package com.dayi.contactlist.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StringDeserializer


class BusinessModel (
    @JsonProperty("_id") id: Int = 0,
    @JsonProperty("_name") name: String = "",
    @JsonProperty("_email") email: String = "",
    @JsonProperty("_phoneNumber") phoneNumber: String = "",
    @JsonProperty("_addressStreet") addressStreet: String = "",
    @JsonProperty("_addressCity") addressCity: String = "",
    @JsonProperty("_addressState") addressState: String = "",
    @JsonProperty("_addressPostalCode") addressPostalCode: String = "",
    @JsonProperty("_isFavorite") isFavorite: Boolean = false,
    @JsonProperty("_webURL") private var webURL: String = "",
    @JsonProperty("_myOpinionRating") private var myOpinionRating: Int = 0,
    @JsonProperty("_daysOpen") private var daysOpen: Array<Boolean> = arrayOf(true, true, true, true, true, true, true),   @JsonProperty("_socialMediaLinks")
    private var socialMediaLinks: MutableMap<String, String> = mutableMapOf(
        "Facebook" to "https://facebook.com/",
        "X" to "https://x.com/"
    )
) : ContactModel(id, name, email, phoneNumber, addressStreet, addressCity, addressState, addressPostalCode) {

    // No-arg constructor for Jackson
    constructor() : this(
        id = 0,
        name = "",
        email = "",
        phoneNumber = "",
        addressStreet = "",
        addressCity = "",
        addressState = "",
        addressPostalCode = "",
        isFavorite = false,
        webURL = "",
        myOpinionRating = 0,
        daysOpen = arrayOf(true, true, true, true, true, true, true),
        socialMediaLinks = mutableMapOf("Facebook" to "https://facebook.com/", "X" to "https://x.com/")
    )

    var _webURL: String
        get() = webURL
        set(value) { webURL = value }

    var _myOpinionRating: Int
        get() = myOpinionRating
        set(value) { myOpinionRating = value }


    var _daysOpen: Array<Boolean>
        get() = daysOpen
         set(value) {daysOpen = value}

    var _socialMediaLinks: MutableMap<String, String>
        get() = socialMediaLinks
       set(value) { socialMediaLinks = value }


    override fun toString(): String {
        return super.toString() + "webURL: $webURL\n" +
                "myOpinionRating: $myOpinionRating\n" +
                "daysOpen: ${daysOpen.contentToString()}\n" +
                "Social Media Links: ${socialMediaLinks.entries.joinToString(", ") { "${it.key}: ${it.value}" }}"
    }

    override fun copy(): BusinessModel {
        return BusinessModel(
            super._id, super._name, super._email, super._phoneNumber, super._addressStreet,
            super._addressCity, super._addressState, super._addressPostalCode, super._isFavorite,
            webURL, myOpinionRating, daysOpen.copyOf(), socialMediaLinks.toMutableMap()
        )
    }
}
