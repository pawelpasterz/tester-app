package com.applause.task

import com.applause.task.config.mongo.AbstractMongoDBTest
import com.applause.task.dto.ExperiencedTester
import com.applause.task.dto.TesterRequest
import com.applause.task.model.Country
import com.applause.task.model.DeviceName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

class TesterControllerTest : AbstractMongoDBTest() {

    @Autowired
    private lateinit var wac: WebApplicationContext
    @Autowired
    private lateinit var mapper: ObjectMapper
    private lateinit var mvc: MockMvc

    @Before
    fun setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @Test
    fun `should return empty array if nothing in db`() {
        val stringResult = mvc.perform(get("/testers"))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty array if nothing in db -- with JSON`() {
        val allRequest = TesterRequest(countries = listOf(Country.ALL), devices = listOf(DeviceName.ALL))
        val stringResult = mvc.perform(
                get("/testers/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(allRequest)))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return sorted list of testers`() {
        testerCollection.insertMany(testers)

        val stringResult = mvc.perform(
                get("/testers/"))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertTrue(result.isNotEmpty())
        assertTrue(verifyOrder(result))
        assertEquals(result.first().experience, 300)
    }

    @Test
    fun `should return sorted list of testers -- JSON`() {
        testerCollection.insertMany(testers)

        val stringResult = mvc.perform(
                get("/testers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{
                            "countries": ["ALL"],
                            "devices": ["ALL"]
                        }""".trimIndent()))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertEquals(result.size, 3)
        assertTrue(verifyOrder(result))
        assertEquals(result.first().experience, 300)
    }

    @Test
    fun `should return result for all devices if option ALL is used, despite other`() {
        testerCollection.insertMany(testers)

        val stringResult = mvc.perform(
                get("/testers/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{
                            "countries": ["ALL"],
                            "devices": ["ALL", "iPhone 5"]
                        }""".trimIndent()))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertEquals(result.size, 3)
        assertTrue(verifyOrder(result))
        assertEquals(result.first().experience, 300)
    }

    @Test
    fun `should return result for all countries if option ALL is used, despite other`() {
        testerCollection.insertMany(testers)

        val stringResult = mvc.perform(
                get("/testers/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{
                            "countries": ["ALL", "US"],
                            "devices": ["ALL"]
                        }""".trimIndent()))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertEquals(result.size, 3)
        assertTrue(verifyOrder(result))
        assertEquals(result.first().experience, 300)
    }

    @Test
    fun `should return one result for GB and iPhone 5`() {
        testerCollection.insertMany(testers)

        val stringResult = mvc.perform(
                get("/testers/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{
                            "countries": ["GB"],
                            "devices": ["iPhone 5"]
                        }""".trimIndent()))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertEquals(result.size, 1)
        assertTrue(verifyOrder(result))
        assertEquals(result.first().experience, 10)
    }

    @Test
    fun `should return two results for ALL and iPhone 3`() {
        testerCollection.insertMany(testers)

        val stringResult = mvc.perform(
                get("/testers/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{
                            "countries": ["ALL"],
                            "devices": ["iPhone 3"]
                        }""".trimIndent()))
                .andExpect { status().isOk }
                .andReturn()
                .response
                .contentAsString

        val result = mapper.readValue<List<ExperiencedTester>>(stringResult)

        assertEquals(result.size, 2)
        assertTrue(verifyOrder(result))
        assertEquals(result.first().experience, 200)
    }
}

private fun verifyOrder(list: List<ExperiencedTester>) = list.asSequence().zipWithNext { first, second -> first.experience >= second.experience }.all { it }