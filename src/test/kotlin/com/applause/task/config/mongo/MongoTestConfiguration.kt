package com.applause.task.config.mongo

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.litote.kmongo.KMongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile


@Configuration
@Profile("test")
class MongoTestConfiguration(private val mongoProperties: MongoProperties) {

    companion object {
        private val starter = MongodStarter.getDefaultInstance()
        private const val ip = "localhost"
        private const val port = 27017
    }

    @Bean
    @DependsOn("mongoTestEcosystem")
    fun mongoClient(): MongoClient = KMongo.createClient(ip, port)

    @Bean
    fun mongoTestEcosystem(): MongoTestEcosystem {
        val exec = starter.prepare(MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(ip, port, Network.localhostIsIPv6()))
                .build())
        val process = exec.start()
        val client = KMongo.createClient(ip, port)
        val databases = mongoProperties.database.values.map { it to client.getDatabase(it) }.toMap()

        return MongoTestEcosystem(exec, process, client, databases)
    }
}

data class MongoTestEcosystem(
        private val exec: MongodExecutable,
        private val process: MongodProcess,
        private val client: MongoClient,
        val databases: Map<String, MongoDatabase>
) {
    fun stopMongo(): Unit {
        process.stop()
        exec.stop()
    }

    fun cleanDatabases() {
        databases.values.forEach { it.drop() }
    }
}