package com.applause.task

import com.applause.task.dao.TesterDAO
import com.applause.task.dto.ExperiencedTester
import com.applause.task.dto.TesterRequest
import com.applause.task.model.Country
import com.applause.task.model.DeviceName
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/testers")
class TesterController(private val testerDAO: TesterDAO) {

    @GetMapping
    fun getBestTesters(@RequestParam(required = false) countries: List<String>?,
                       @RequestParam(required = false) devices: List<String>?): List<ExperiencedTester> {

        val request = TesterRequest(
                countries = Country.getListOfCountriesFromString(countries),
                devices = DeviceName.getListOfDevicesFromString(devices)
        )

        return testerDAO.getTestersByExperience(request)
    }

    @GetMapping("/json")
    fun getBestTestersWithByJSON(@RequestBody request: TesterRequest) = testerDAO.getTestersByExperience(request.checkForAll())
}