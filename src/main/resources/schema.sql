-- Create the currency_conversions table
CREATE TABLE IF NOT EXISTS currency_conversions (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(20, 8) NOT NULL,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    converted_amount DECIMAL(20, 8) NOT NULL,
    exchange_rate DECIMAL(20, 8) NOT NULL,
    user_id BIGINT,
    user_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_currency_conversions_user_id ON currency_conversions(user_id);
CREATE INDEX IF NOT EXISTS idx_currency_conversions_created_at ON currency_conversions(created_at);
CREATE INDEX IF NOT EXISTS idx_currency_conversions_currencies ON currency_conversions(from_currency, to_currency); 