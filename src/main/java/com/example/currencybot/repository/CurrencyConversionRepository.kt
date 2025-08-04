package com.example.currencybot.repository

import com.example.currencybot.entity.CurrencyConversion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CurrencyConversionRepository : JpaRepository<CurrencyConversion, Long> {
    
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<CurrencyConversion>
    
    fun findTop10ByOrderByCreatedAtDesc(): List<CurrencyConversion>
    
    @Query("""
        SELECT c.fromCurrency, c.toCurrency, COUNT(c) as count 
        FROM CurrencyConversion c 
        GROUP BY c.fromCurrency, c.toCurrency 
        ORDER BY count DESC
    """)
    fun getConversionStats(): List<Array<Any>>
    
    fun deleteByCreatedAtBefore(cutoffDate: LocalDateTime): Long
} 