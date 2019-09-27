package com.applause.task.config.mongo

import com.applause.task.model.Country
import com.applause.task.model.Device
import com.applause.task.model.DeviceName
import com.applause.task.model.Tester
import com.mongodb.client.MongoCollection
import junit.framework.TestCase
import org.junit.After
import org.junit.runner.RunWith
import org.litote.kmongo.getCollection
import org.litote.kmongo.newId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@SpringBootTest
abstract class AbstractMongoDBTest : TestCase() {

    @Autowired
    internal lateinit var mongoEcosystem: MongoTestEcosystem

    internal val testerCollection: MongoCollection<Tester>
        get() = mongoEcosystem.databases["applause"]!!.getCollection<Tester>("testerInfos")

    internal val simpleTester: Tester
        get() = Tester(newId(), 1, "anyTester", "anySurname", Country.values().random(), Date(), listOf(Device(DeviceName.H1.id, DeviceName.H1.value, 666)))

    internal val testers = listOf(
            simpleTester.copy(firstName = "anyName_1",testerId = 1, country = Country.GB, devices = listOf(
                    Device(DeviceName.N4.id, DeviceName.N4.value, 12),
                    Device(DeviceName.IP5.id, DeviceName.IP5.value, 10))),
            simpleTester.copy(firstName = "anyName_2", testerId = 2, country = Country.GB, devices = listOf(
                    Device(DeviceName.N4.id, DeviceName.N4.value, 20),
                    Device(DeviceName.IP3.id, DeviceName.IP3.value, 30))),
            simpleTester.copy(firstName = "anyName_3", testerId = 3, country = Country.US, devices = listOf(
                    Device(DeviceName.N4.id, DeviceName.N4.value, 100),
                    Device(DeviceName.IP3.id, DeviceName.IP3.value, 200)))
    )

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
        mongoEcosystem.stopMongo()
    }

    @After
    fun cleanDatabase() {
        mongoEcosystem.cleanDatabases()
    }
}