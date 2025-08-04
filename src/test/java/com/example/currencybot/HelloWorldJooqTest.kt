package com.example.currencybot

import org.assertj.core.api.Assertions.assertThat
import org.jooq.Record1
import org.junit.jupiter.api.Test

class HelloWorldJooqTest : AbstractJooqIntegrationTest() {

    @Test
    fun helloWorld() {
        val result = dsl.fetchOne("SELECT 1") as Record1<Int>
        assertThat(result.value1()).isEqualTo(1)
    }
} 