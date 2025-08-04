package com.example.currencybot.config

import com.example.currencybot.bot.MyTelegramBot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.slf4j.LoggerFactory

@Configuration
open class BotConfig {

    private val logger = LoggerFactory.getLogger(BotConfig::class.java)

    @Bean
    open fun telegramBotsApi(myTelegramBot: MyTelegramBot): TelegramBotsApi {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        try {
            telegramBotsApi.registerBot(myTelegramBot)
            logger.info("Succes")
        } catch (e: TelegramApiException) {
            logger.error("Error registering bot: ${e.message}", e)
        }
        return telegramBotsApi
    }
} 