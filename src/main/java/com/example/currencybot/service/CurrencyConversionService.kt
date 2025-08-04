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

    /**
     * Save a currency conversion to the database using JOOQ
     */
    @Transactional
    open fun saveConversion(conversion: CurrencyConversion): Long {
        val result = dslContext.insertInto(DSL.table("currency_conversions"))
            .columns(
                DSL.field("amount"),
                DSL.field("from_currency"),
                DSL.field("to_currency"),
                DSL.field("converted_amount"),
                DSL.field("exchange_rate"),
                DSL.field("user_id"),
                DSL.field("user_name"),
                DSL.field("created_at")
            )
            .values(
                conversion.amount,
                conversion.fromCurrency,
                conversion.toCurrency,
                conversion.convertedAmount,
                conversion.exchangeRate,
                conversion.userId,
                conversion.userName,
                conversion.createdAt
            )
            .returning(DSL.field("id"))
            .fetchOne()

        return result?.get(0) as Long? ?: throw RuntimeException("Failed to save conversion")
    }

    /**
     * Get all conversions for a specific user
     */
    fun getConversionsByUser(userId: Long): List<CurrencyConversion> {
        val result: Result<Record> = dslContext.select()
            .from(DSL.table("currency_conversions"))
            .where(DSL.field("user_id").eq(userId))
            .orderBy(DSL.field("created_at").desc())
            .fetch()

        return result.map { record ->
            mapRecordToCurrencyConversion(record)
        }
    }

    /**
     * Get recent conversions (last 10)
     */
    fun getRecentConversions(limit: Int = 10): List<CurrencyConversion> {
        val result: Result<Record> = dslContext.select()
            .from(DSL.table("currency_conversions"))
            .orderBy(DSL.field("created_at").desc())
            .limit(limit)
            .fetch()

        return result.map { record ->
            mapRecordToCurrencyConversion(record)
        }
    }

    /**
     * Get conversion statistics by currency pair
     */
    fun getConversionStats(): Map<String, Int> {
        val result: Result<Record3<in Any, in Any, Int?>?> = dslContext.select(
            DSL.field("from_currency"),
            DSL.field("to_currency"),
            DSL.count().`as`("count")
        )
            .from(DSL.table("currency_conversions"))
            .groupBy(DSL.field("from_currency"), DSL.field("to_currency"))
            .orderBy(DSL.field("count").desc())
            .fetch()

        return result.associate { record ->
            val fromCurrency = record?.get("from_currency") as String
            val toCurrency = record.get("to_currency") as String
            val count = record.get("count") as Long
            "$fromCurrency/$toCurrency" to count.toInt()
        }
    }

    /**
     * Delete old conversions (older than specified days)
     */
    @Transactional
    open fun deleteOldConversions(daysOld: Int): Int {
        val cutoffDate = LocalDateTime.now().minusDays(daysOld.toLong())

        return dslContext.deleteFrom(DSL.table("currency_conversions"))
            .where(DSL.field("created_at").lt(cutoffDate))
            .execute()
    }

    /**
     * Helper method to map JOOQ Record to CurrencyConversion entity
     */
    private fun mapRecordToCurrencyConversion(record: Record): CurrencyConversion {
        return CurrencyConversion(
            id = record.get("id") as Long?,
            amount = record.get("amount") as BigDecimal,
            fromCurrency = record.get("from_currency") as String,
            toCurrency = record.get("to_currency") as String,
            convertedAmount = record.get("converted_amount") as BigDecimal,
            exchangeRate = record.get("exchange_rate") as BigDecimal,
            userId = record.get("user_id") as Long?,
            userName = record.get("user_name") as String?,
            createdAt = record.get("created_at") as LocalDateTime
        )
    }
} 