package com.applause.task.repository

import com.applause.task.dto.ExperiencedTester
import com.applause.task.model.Country

interface TesterRepository {

    fun getTestersByDeviceSortedByExperience(countries: List<Country>, ids: List<Long>): List<ExperiencedTester>
}