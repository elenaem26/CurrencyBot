package com.example.currencybot.service

import com.example.currencybot.entity.CurrencyConversion
import com.example.currencybot.repository.CurrencyConversionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class CurrencyConversionService(private val repository: CurrencyConversionRepository) {

    /**
     * Save a currency conversion to the database using Hibernate
     */
    @Transactional
    fun saveConversion(conversion: CurrencyConversion): Long {
        val savedConversion = repository.save(conversion)
        return savedConversion.id ?: throw RuntimeException("Failed to save conversion")
    }

    /**
     * Get all conversions for a specific user
     */
    fun getConversionsByUser(userId: Long): List<CurrencyConversion> {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    /**
     * Get recent conversions (last 10)
     */
    fun getRecentConversions(limit: Int = 10): List<CurrencyConversion> {
        return repository.findTop10ByOrderByCreatedAtDesc().take(limit)
    }

    /**
     * Get conversion statistics by currency pair
     */
    fun getConversionStats(): Map<String, Int> {
        val stats = repository.getConversionStats()
        return stats.associate { row ->
            val fromCurrency = row[0] as String
            val toCurrency = row[1] as String
            val count = (row[2] as Long).toInt()
            "$fromCurrency/$toCurrency" to count
        }
    }

    /**
     * Delete old conversions (older than specified days)
     */
    @Transactional
    fun deleteOldConversions(daysOld: Int): Int {
        val cutoffDate = LocalDateTime.now().minusDays(daysOld.toLong())
        return repository.deleteByCreatedAtBefore(cutoffDate).toInt()
    }
} 