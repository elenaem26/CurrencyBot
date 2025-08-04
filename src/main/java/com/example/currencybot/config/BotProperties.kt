package com.example.currencybot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "telegrambots")
data class BotProperties(
    var enabled: Boolean = true,
    var bots: List<BotConfig> = emptyList()
) {
    data class BotConfig(
        var username: String = "",
        var token: String = ""
    )
}