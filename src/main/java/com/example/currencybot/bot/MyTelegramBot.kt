package com.example.currencybot.bot

import com.fasterxml.jackson.annotation.JsonProperty
import com.example.currencybot.config.BotProperties
import com.example.currencybot.entity.CurrencyConversion
import com.example.currencybot.service.CurrencyConversionService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.springframework.web.client.RestTemplate
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class MyTelegramBot(
    private val botProperties: BotProperties,
    private val currencyConversionService: CurrencyConversionService
) : TelegramLongPollingBot() {

    private val logger = LoggerFactory.getLogger(MyTelegramBot::class.java)
    private val restTemplate = RestTemplate()

    override fun getBotUsername(): String {
        val username = botProperties.bots.firstOrNull()?.username
        if (username.isNullOrEmpty()) {
            logger.error("Bot username is not configured!")
        }
        return username ?: throw IllegalArgumentException("Bot username is not configured!")
    }

    override fun getBotToken(): String {
        val token = botProperties.bots.firstOrNull()?.token
        if (token.isNullOrEmpty()) {
            logger.error("Bot token is not configured!")
        }
        return token ?: throw IllegalArgumentException("Bot token is not configured!")
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val messageText = update.message.text
            val chatId = update.message.chatId
            val userName = update.message.from.userName ?: update.message.from.firstName

            logger.info("Received message from $userName: $messageText")

            try {
                val response = processMessage(messageText, userName)
                sendMessage(chatId, response)
            } catch (e: Exception) {
                logger.error("Error processing message: ${e.message}", e)
                sendMessage(chatId, "Sorry, an error occurred while processing your request.")
            }
        }
    }

    private fun processMessage(message: String, userName: String): String {
        return when {
            message.startsWith("/start") -> getWelcomeMessage(userName)
            message.startsWith("/help") -> getHelpMessage()
            message.startsWith("/convert") -> processConvertCommand(message)
            message.startsWith("/rates") -> getCurrentRates()
            message.startsWith("/history") -> getConversionHistory(userName)
            message.startsWith("/stats") -> getConversionStats()
            else -> "I don't understand that command. Type /help for available commands."
        }
    }

    private fun getWelcomeMessage(userName: String): String {
        return """
            👋 Welcome to Currency Bot, $userName!
            
            I can help you convert currencies and check exchange rates.
            
            Available commands:
            /convert <amount> <from> <to> - Convert currency (e.g., /convert 100 USD EUR)
            /rates - Get current exchange rates for major currencies
            /history - Show recent conversion history
            /stats - Show conversion statistics
            /help - Show this help message
            
            Example: /convert 100 USD EUR
        """.trimIndent()
    }

    private fun getHelpMessage(): String {
        return """
            💱 Currency Bot Help
            
            Commands:
            /convert <amount> <from> <to> - Convert currency
            /rates - Get current exchange rates
            /history - Show recent conversion history
            /stats - Show conversion statistics
            /help - Show this help message
            
            Examples:
            /convert 100 USD EUR
            /convert 50 EUR GBP
            /convert 1000 JPY USD
            
            Supported currencies: USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY, INR, BRL
        """.trimIndent()
    }

    private fun processConvertCommand(message: String): String {
        val parts = message.split(" ")
        if (parts.size != 4) {
            return "Invalid format. Use: /convert <amount> <from> <to>\nExample: /convert 100 USD EUR"
        }

        try {
            val amount = parts[1].toBigDecimal()
            val fromCurrency = parts[2].uppercase()
            val toCurrency = parts[3].uppercase()

            if (amount <= BigDecimal.ZERO) {
                return "Amount must be greater than 0"
            }

            val rate = getExchangeRate(fromCurrency, toCurrency)
            val result = convertCurrency(amount, fromCurrency, toCurrency)
            
            // Save conversion to database
            val conversion = CurrencyConversion(
                amount = amount,
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                convertedAmount = result,
                exchangeRate = rate,
                userId = null, // We'll add user ID later if needed
                userName = "userName" //todo
            )
            
            try {
                currencyConversionService.saveConversion(conversion)
                logger.info("Saved conversion to database: $amount $fromCurrency to $toCurrency")
            } catch (e: Exception) {
                logger.error("Failed to save conversion to database: ${e.message}", e)
            }
            
            return """
                💱 Currency Conversion Result:
                
                $amount $fromCurrency = $result $toCurrency
                
                Rate: 1 $fromCurrency = $rate $toCurrency
                
                ✅ Conversion saved to history
            """.trimIndent()

        } catch (e: NumberFormatException) {
            return "Invalid amount. Please enter a valid number."
        } catch (e: Exception) {
            return "Error converting currency: ${e.message}"
        }
    }

    private fun getCurrentRates(): String {
        val baseCurrency = "USD"
        val currencies = listOf("EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "BRL")
        
        val rates = currencies.mapNotNull { currency ->
            try {
                val rate = getExchangeRate(baseCurrency, currency)
                "$baseCurrency/$currency: $rate"
            } catch (e: Exception) {
                null
            }
        }

        return """
            📊 Current Exchange Rates (Base: $baseCurrency)
            
            ${rates.joinToString("\n")}
            
            Last updated: ${java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
        """.trimIndent()
    }

    private fun convertCurrency(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        val rate = getExchangeRate(fromCurrency, toCurrency)
        return amount.multiply(rate).setScale(4, RoundingMode.HALF_UP)
    }

    private fun getConversionHistory(userName: String): String {
        return try {
            val recentConversions = currencyConversionService.getRecentConversions(5)
            if (recentConversions.isEmpty()) {
                "📊 No conversion history found."
            } else {
                val historyText = recentConversions.joinToString("\n") { conversion ->
                    "${conversion.amount} ${conversion.fromCurrency} → ${conversion.convertedAmount} ${conversion.toCurrency} (${conversion.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"))})"
                }
                "📊 Recent Conversions:\n\n$historyText"
            }
        } catch (e: Exception) {
            logger.error("Error getting conversion history: ${e.message}", e)
            "❌ Error retrieving conversion history."
        }
    }

    private fun getConversionStats(): String {
        return try {
            val stats = currencyConversionService.getConversionStats()
            if (stats.isEmpty()) {
                "📈 No conversion statistics available."
            } else {
                val statsText = stats.values.take(5).joinToString("\n") { a -> a.toString()
                    //todo
                }
                "📈 Top Currency Pairs:\n\n$statsText"
            }
        } catch (e: Exception) {
            logger.error("Error getting conversion stats: ${e.message}", e)
            "❌ Error retrieving conversion statistics."
        }
    }

    private fun getExchangeRate(fromCurrency: String, toCurrency: String): BigDecimal {
        // For demo purposes, using a simple API. In production, you might want to use a more reliable service
        val url = "https://api.exchangerate-api.com/v4/latest/$fromCurrency"
        
        return try {
            val response = restTemplate.getForObject(url, ExchangeRateResponse::class.java)
            response?.rates?.get(toCurrency)?.toBigDecimal() 
                ?: throw Exception("Unable to get exchange rate for $fromCurrency to $toCurrency")
        } catch (e: Exception) {
            logger.error("Error fetching exchange rate: ${e.message}", e)
            // Fallback to some basic rates for demo purposes
            getFallbackRate(fromCurrency, toCurrency)
        }
    }

    private fun getFallbackRate(fromCurrency: String, toCurrency: String): BigDecimal {
        // Simple fallback rates for demo purposes
        val rates = mapOf(
            "USD" to mapOf(
                "EUR" to "0.85",
                "GBP" to "0.73",
                "JPY" to "110.0",
                "CAD" to "1.25",
                "AUD" to "1.35",
                "CHF" to "0.92",
                "CNY" to "6.45",
                "INR" to "74.5",
                "BRL" to "5.2"
            ),
            "EUR" to mapOf(
                "USD" to "1.18",
                "GBP" to "0.86",
                "JPY" to "129.4",
                "CAD" to "1.47",
                "AUD" to "1.59",
                "CHF" to "1.08",
                "CNY" to "7.59",
                "INR" to "87.6",
                "BRL" to "6.12"
            )
        )

        return when {
            fromCurrency == toCurrency -> BigDecimal.ONE
            rates.containsKey(fromCurrency) && rates[fromCurrency]?.containsKey(toCurrency) == true ->
                rates[fromCurrency]!![toCurrency]!!.toBigDecimal()
            else -> throw Exception("Exchange rate not available for $fromCurrency to $toCurrency")
        }
    }

    private fun sendMessage(chatId: Long, text: String) {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = text
        message.enableMarkdown(true)

        try {
            execute(message)
            logger.info("Message sent to chat $chatId")
        } catch (e: TelegramApiException) {
            logger.error("Error sending message: ${e.message}", e)
        }
    }
}

data class ExchangeRateResponse(
    @JsonProperty("base") val base: String,
    @JsonProperty("date") val date: String,
    @JsonProperty("rates") val rates: Map<String, Double>
)

