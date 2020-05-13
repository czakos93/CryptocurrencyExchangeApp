package pl.dawid.bronczak.cryptocurrencyexchangeapp.client;

import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.math.BigDecimal;
import java.util.Map;

public interface CryptocurrencyApiClient {

	BigDecimal fetchCryptocurrencyExchangeRate(CurrencyType baseCurrency, CurrencyType currencyToCheckExchangeRate);

	Map<CurrencyType, BigDecimal> fetchExchangeRatesForAllCurrencies(CurrencyType baseCurrency);
}
