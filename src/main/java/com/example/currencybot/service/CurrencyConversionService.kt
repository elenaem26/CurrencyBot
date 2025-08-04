package com.example.currencybot.service

import com.example.currencybot.entity.CurrencyConversion
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Record3
import org.jooq.Result
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
open class CurrencyConversionService(private val dslContext: DSLContext) {


} 