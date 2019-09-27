package com.applause.task.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonValue
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.Date

data class Tester(
        @BsonId val _id: Id<Tester> = newId(),
        val testerId: Long,
        val firstName: String,
        val lastName: String,
        val country: Country,
        val lastLogin: Date,
        val devices: List<Device>
)

enum class Country {
    US,
    GB,
    JP,
    @JsonEnumDefaultValue
    ALL;

    companion object {
        fun getListOfCountriesFromString(countries: List<String>?) =
                countries?.map { enumValueOf<Country>(it) }
                        .takeUnless { it?.contains(ALL) ?: true } ?: getAllCountries()

        fun getAllCountries() = values().filter { it != ALL }
    }
}

data class Device(
        val id: Long,
        val name: String,
        val quantity: Int
)

enum class DeviceName(@JsonValue val value: String) {
    IP3("iPhone 3"),
    IP4("iPhone 4"),
    IP4S("iPhone 4S"),
    IP5("iPhone 5"),
    GS3("Galaxy S3"),
    GS4("Galaxy S4"),
    N4("Nexus 4"),
    DR("Droid Razor"),
    DD("Droid DNA"),
    H1("HTC One"),
    ALL("ALL");

    val id: Long
        get() = idsMap[this]!!

    companion object {
        private val idsMap = mapOf<DeviceName, Long>(
                IP4 to 1,
                IP4S to 2,
                IP5 to 3,
                GS3 to 4,
                GS4 to 5,
                N4 to 6,
                DR to 7,
                DD to 8,
                H1 to 9,
                IP3 to 10,
                ALL to -1
        )

        private val valuesMap = values().associateBy { it.value }

        operator fun get(key: String) =
                valuesMap[key] ?: throw IllegalArgumentException("Incompatible device")

        fun getListOfDevicesFromString(devices: List<String>?) =
                devices?.map { DeviceName[it] }
                        .takeUnless { it?.contains(ALL) ?: true } ?: getAllDevices()

        fun getAllDevices() = values().filter { it != ALL }

    }
}