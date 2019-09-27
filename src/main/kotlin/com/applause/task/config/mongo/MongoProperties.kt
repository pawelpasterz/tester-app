package com.applause.task.config.mongo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("task.mongo")
class MongoProperties {
        lateinit var servers: String
        lateinit var credentials: Map<String, String?>
        var uri: String? = null
        lateinit var database: Map<String, String>
}