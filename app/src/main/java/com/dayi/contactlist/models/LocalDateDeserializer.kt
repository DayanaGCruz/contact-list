package com.dayi.contactlist.models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.LocalDate
import java.time.Month

class LocalDateDeserializer : JsonDeserializer<LocalDate>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate {
        val node = p.codec.readTree<com.fasterxml.jackson.databind.JsonNode>(p)
        val year = node["year"].asInt()
        val month = Month.valueOf(node["month"].asText())
        val day = node["dayOfMonth"].asInt()
        return LocalDate.of(year, month, day)
    }
}