package com.applause.task.config.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import com.applause.task.config.docker.ConditionalOnDockerEnv
import com.applause.task.config.docker.ConditionalOnNormalEnv
import org.litote.kmongo.KMongo
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MongoProperties::class)
class MongoConfig(private val mongoProperties: MongoProperties) {

    @Bean("mongoClient")
    @ConditionalOnNormalEnv
    fun mongoClientDocker(): MongoClient {
        val serverList = mongoProperties.servers
                .split(",")
                .map { ServerAddress(it) }
                .toList()

        return mongoProperties.credentials["username"]?.let {
            if (it.isBlank()) return@let null
            val username = it
            val password = mongoProperties.credentials["password"] ?: ""
            val credentialList = listOf(MongoCredential.createScramSha1Credential(username, "admin", password.toCharArray()))

            return KMongo.createClient(serverList, credentialList)
        } ?: KMongo.createClient(serverList)
    }

    @Bean("mongoClient")
    @ConditionalOnDockerEnv
    fun mongoClientNormal(): MongoClient {
        val uri = mongoProperties.uri ?: throw IllegalArgumentException("Missing URI for MongoDB")
        return KMongo.createClient(MongoClientURI(uri))
    }

    @Bean("testDB")
    fun testDatabase(mongoClient: MongoClient): MongoDatabase = mongoClient.getDatabase(mongoProperties.database["test"]!!)
}