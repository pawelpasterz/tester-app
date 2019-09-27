package com.applause.task.dao

import com.applause.task.dto.TesterRequest
import com.applause.task.repository.TesterRepository
import org.springframework.stereotype.Component

@Component
class TesterDAO(private val repository: TesterRepository) {

    fun getTestersByExperience(request: TesterRequest) =
            repository.getTestersByDeviceSortedByExperience(request.countries, request.devices.map { it.id })
}