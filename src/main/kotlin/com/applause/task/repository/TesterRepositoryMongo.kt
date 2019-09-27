package com.applause.task.repository

import com.applause.task.dto.ExperiencedTester
import com.applause.task.model.Country
import com.applause.task.model.Device
import com.applause.task.model.Tester
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Accumulators.first
import com.mongodb.client.model.UnwindOptions
import org.litote.kmongo.`in`
import org.litote.kmongo.aggregate
import org.litote.kmongo.and
import org.litote.kmongo.descending
import org.litote.kmongo.div
import org.litote.kmongo.elemMatch
import org.litote.kmongo.fields
import org.litote.kmongo.from
import org.litote.kmongo.getCollection
import org.litote.kmongo.group
import org.litote.kmongo.include
import org.litote.kmongo.match
import org.litote.kmongo.project
import org.litote.kmongo.sort
import org.litote.kmongo.sum
import org.litote.kmongo.unwind
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty

@Component
class TesterRepositoryMongo(testDB: MongoDatabase) : TesterRepository {

    private val userCollection = testDB.getCollection<Tester>("testerInfos")

    override fun getTestersByDeviceSortedByExperience(countries: List<Country>, ids: List<Long>): List<ExperiencedTester> =
            userCollection.aggregate<ExperiencedTester>(
                    match(
                            and(
                                    Tester::country `in` countries,
                                    Tester::devices elemMatch and(Device::id `in` ids)
                            )
                    ),
                    unwind(Tester::devices),
                    match(
                            and(
                                    Tester::devices / Device::id `in` ids
                            )
                    ),
                    project(fields(
                            include(Tester::_id, Tester::country, Tester::firstName, Tester::lastLogin, Tester::lastName, Tester::testerId),
                            Tester::devices from Tester::devices / Device::quantity
                    )),
                    group(
                            Tester::testerId,
                            first(ExperiencedTester::name.name, Tester::firstName),
                            first(ExperiencedTester::surname.name, Tester::lastName),
                            ExperiencedTester::experience sum Tester::devices
                    ),
                    project(fields(
                            include(
                                    ExperiencedTester::name,
                                    ExperiencedTester::surname,
                                    ExperiencedTester::experience)
                    )),
                    sort(descending(ExperiencedTester::experience))
            ).toList()
}

private fun <T> unwind(property: KProperty<T>, unwindOptions: UnwindOptions = UnwindOptions()) = unwind(fieldName = "\$${property.name}", unwindOptions = unwindOptions)
