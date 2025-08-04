package com.example.currencybot

import com.example.currencybot.entity.CurrencyConversion
import com.example.currencybot.service.CurrencyConversionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class CurrencyConversionIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var currencyConversionService: CurrencyConversionService

    @Test
    fun shouldSaveAndRetrieveCurrencyConversion() {
        // Given
        val conversion = CurrencyConversion(
            amount = BigDecimal("100.00"),
            fromCurrency = "USD",
            toCurrency = "EUR",
            convertedAmount = BigDecimal("85.00"),
            exchangeRate = BigDecimal("0.85"),
            userId = 123L,
            userName = "test_user"
        )

        // When
        val savedId = currencyConversionService.saveConversion(conversion)
        val recentConversions = currencyConversionService.getRecentConversions(5)

        // Then
        assertThat(savedId).isNotNull()
        assertThat(savedId).isGreaterThan(0)
        assertThat(recentConversions).isNotEmpty()
        
        val savedConversion = recentConversions.first()
        assertThat(savedConversion.amount).isEqualTo(conversion.amount)
        assertThat(savedConversion.fromCurrency).isEqualTo(conversion.fromCurrency)
        assertThat(savedConversion.toCurrency).isEqualTo(conversion.toCurrency)
    }
} 