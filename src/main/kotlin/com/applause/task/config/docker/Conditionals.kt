package com.applause.task.config.docker

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@ConditionalOnProperty(value = ["task.docker.enabled"])
@Profile("!test")
annotation class ConditionalOnDockerEnv

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@ConditionalOnExpression("!\${task.docker.enabled:false}")
@Profile("!test")
annotation class ConditionalOnNormalEnv