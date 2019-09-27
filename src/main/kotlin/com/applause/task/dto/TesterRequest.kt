package com.applause.task.dto

import com.applause.task.model.Country
import com.applause.task.model.DeviceName
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TesterRequest(
        val countries: List<Country>,
        val devices: List<DeviceName>
) {
    fun checkForAll() = copy(
            countries = countries.takeUnless { it.contains(Country.ALL) } ?: Country.getAllCountries(),
            devices = devices.takeUnless { it.contains(DeviceName.ALL) } ?: DeviceName.getAllDevices()
    )
}