package com.example.currencybot.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "currency_conversions")
data class CurrencyConversion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val amount: BigDecimal,
    
    @Column(name = "from_currency", nullable = false, length = 3)
    val fromCurrency: String,
    
    @Column(name = "to_currency", nullable = false, length = 3)
    val toCurrency: String,
    
    @Column(name = "converted_amount", nullable = false)
    val convertedAmount: BigDecimal,
    
    @Column(name = "exchange_rate", nullable = false)
    val exchangeRate: BigDecimal,
    
    @Column(name = "user_id")
    val userId: Long?,
    
    @Column(name = "user_name")
    val userName: String?,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()


) 