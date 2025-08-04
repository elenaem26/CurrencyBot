package com.example.currencybot.config

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
open class JooqConfig {

    @Autowired
    private lateinit var dataSource: DataSource

    @Bean
    open fun dslContext(): DSLContext {
        return DSL.using(dataSource, SQLDialect.POSTGRES)
    }
} 