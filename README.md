# Currency Bot

A Telegram bot that provides currency conversion and exchange rate information.

## Features

- 💱 Real-time currency conversion
- 📊 Current exchange rates for major currencies
- 🚀 Easy-to-use commands
- 🔄 Fallback rates when API is unavailable

## Commands

- `/start` - Welcome message and introduction
- `/help` - Show available commands and examples
- `/convert <amount> <from> <to>` - Convert currency (e.g., `/convert 100 USD EUR`)
- `/rates` - Get current exchange rates for major currencies
- `/history` - Show recent conversion history
- `/stats` - Show conversion statistics

## Supported Currencies

- USD (US Dollar)
- EUR (Euro)
- GBP (British Pound)
- JPY (Japanese Yen)
- CAD (Canadian Dollar)
- AUD (Australian Dollar)
- CHF (Swiss Franc)
- CNY (Chinese Yuan)
- INR (Indian Rupee)
- BRL (Brazilian Real)

## Setup

### Prerequisites

- Java 21 or higher
- Gradle
- PostgreSQL 12 or higher
- Telegram Bot Token (get from [@BotFather](https://t.me/botfather))

### Configuration

1. Create a Telegram bot using [@BotFather](https://t.me/botfather)
2. Copy your bot token and username
3. Set up PostgreSQL database:
   ```sql
   CREATE DATABASE currencybot;
   ```
4. Update database configuration in `application.yml` if needed
5. Create `src/main/resources/application-secrets.yml` with your bot credentials:

```yaml
telegrambots:
  enabled: true
  bots:
    - username: your_bot_username
      token: your_bot_token_here
```

### Running the Application

1. Build the project:
```bash
./gradlew build
```

2. Run the application:
```bash
./gradlew bootRun
```

3. Start a conversation with your bot on Telegram and send `/start`

## API Integration

The bot uses the [Exchange Rate API](https://exchangerate-api.com/) for real-time exchange rates. If the API is unavailable, it falls back to predefined rates for demo purposes.

## Database

The application uses PostgreSQL with Hibernate/JPA for database operations. All currency conversions are stored in the database for history and analytics.

## Project Structure

```
src/main/java/com/example/currencybot/
├── bot/
│   └── MyTelegramBot.kt          # Main bot implementation
├── config/
│   └── BotConfig.kt              # Bot configuration
├── entity/
│   └── CurrencyConversion.kt     # JPA entity
├── repository/
│   └── CurrencyConversionRepository.kt  # JPA repository
├── service/
│   └── CurrencyConversionService.kt     # Business logic
└── CurrencyBotApplication.java   # Spring Boot application
```

## Development

### Adding New Currencies

To add support for new currencies, update the fallback rates in `MyTelegramBot.kt` and the supported currencies list in the help message.

### Customizing Exchange Rate Source

Modify the `getExchangeRate` method in `MyTelegramBot.kt` to use a different exchange rate API or service.

### Running Integration Tests

The project includes integration tests that use TestContainers to run PostgreSQL in Docker:

```bash
# Run all tests
./gradlew test

# Run only integration tests
./gradlew test --tests "*IntegrationTest*"

# Run with Docker containers
./gradlew test --tests "*IntegrationTest*" --info
```

The integration tests verify:
- Database connection and schema creation
- Currency conversion saving and retrieval
- Statistics calculation
- Data cleanup operations

## Error Handling

The bot includes comprehensive error handling for:
- Invalid command formats
- Unsupported currencies
- API failures
- Network issues

## Logging

The application uses SLF4J for logging. Check the console output for bot activity and error messages.

## License

This project is open source and available under the MIT License. 