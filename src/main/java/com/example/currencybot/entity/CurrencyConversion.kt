package com.example.currencybot.entity

import java.math.BigDecimal
import java.time.LocalDateTime

data class CurrencyConversion(
    val id: Long? = null,
    val amount: BigDecimal,
    val fromCurrency: String,
    val toCurrency: String,
    val convertedAmount: BigDecimal,
    val exchangeRate: BigDecimal,
    val userId: Long?,
    val userName: String?,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 