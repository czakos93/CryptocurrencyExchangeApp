package pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency;

import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.CryptocurrencyApiClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastRequest;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastResponse;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeRateResponse;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

public class CryptocurrencyExchangeService {

	private final CryptocurrencyApiClient cryptocurrencyApiClient;
	private final float feePercent;

	public CryptocurrencyExchangeService(CryptocurrencyApiClient cryptocurrencyApiClient, int feePercent) {
		this.cryptocurrencyApiClient = cryptocurrencyApiClient;
		this.feePercent = feePercent;
	}

	public CryptocurrencyExchangeRateResponse getExchangeRate(CurrencyType baseCurrency, Set<CurrencyType> currenciesToCheckExchangeRate) {
		Map<CurrencyType, BigDecimal> currenciesExchangeRates;
		if (isNull(currenciesToCheckExchangeRate) || currenciesToCheckExchangeRate.isEmpty()) {
			currenciesExchangeRates = cryptocurrencyApiClient.fetchCryptocurrencyExchangeRateForAllCryptocurrencies(baseCurrency);
		} else {
			ensureCurrenciesToExchangeNotContainBaseCurrency(baseCurrency, currenciesToCheckExchangeRate);
			currenciesExchangeRates = currenciesToCheckExchangeRate.parallelStream()
					.map(fetchCurrencyExchangeRateFrom(baseCurrency))
					.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
		}

		return new CryptocurrencyExchangeRateResponse(baseCurrency, currenciesExchangeRates);
	}

	public CryptocurrencyExchangeForecastResponse calculateExchangeForecast(CryptocurrencyExchangeForecastRequest cryptocurrencyExchangeForecastRequest) {
		BigDecimal exchangeFee = calculateFee(cryptocurrencyExchangeForecastRequest.getAmount());
		BigDecimal amountMinusFee = cryptocurrencyExchangeForecastRequest.getAmount().subtract(exchangeFee);

		CurrencyType baseCurrency = cryptocurrencyExchangeForecastRequest.getBaseCurrency();
		ensureCurrenciesToExchangeIsNotEmpty(cryptocurrencyExchangeForecastRequest.getCurrenciesToExchange());
		ensureCurrenciesToExchangeNotContainBaseCurrency(baseCurrency, cryptocurrencyExchangeForecastRequest.getCurrenciesToExchange());
		Map<CurrencyType, ExchangeDetails> exchangeResults = cryptocurrencyExchangeForecastRequest.getCurrenciesToExchange().parallelStream()
				.map(fetchCurrencyExchangeRateFrom(baseCurrency))
				.map(prepareExchangeDetails(cryptocurrencyExchangeForecastRequest, exchangeFee, amountMinusFee))
				.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

		return new CryptocurrencyExchangeForecastResponse(cryptocurrencyExchangeForecastRequest.getBaseCurrency(), exchangeResults);
	}

	private BigDecimal calculateFee(BigDecimal amount) {
		return amount
				.multiply(BigDecimal.valueOf(feePercent))
				.divide(BigDecimal.valueOf(100));
	}

	private Function<SimpleEntry<CurrencyType, BigDecimal>, SimpleEntry<CurrencyType, ExchangeDetails>> prepareExchangeDetails(CryptocurrencyExchangeForecastRequest cryptocurrencyExchangeForecastRequest, BigDecimal exchangeFee, BigDecimal amountMinusFee) {
		return currencyExchangeMap -> new SimpleEntry<>(currencyExchangeMap.getKey(),
				new ExchangeDetails(currencyExchangeMap.getValue(), cryptocurrencyExchangeForecastRequest.getAmount(), currencyExchangeMap.getValue().multiply(amountMinusFee), exchangeFee));
	}

	private Function<CurrencyType, SimpleEntry<CurrencyType, BigDecimal>> fetchCurrencyExchangeRateFrom(CurrencyType baseCurrency) {
		return currencyToCheckExchangeRate -> new SimpleEntry<>(currencyToCheckExchangeRate, cryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(baseCurrency, currencyToCheckExchangeRate));
	}

	private void ensureCurrenciesToExchangeNotContainBaseCurrency(CurrencyType baseCurrency, Set<CurrencyType> currenciesToExchange) {
		if (currenciesToExchange.contains(baseCurrency)) {
			throw new IllegalArgumentException("Currencies to exchange contain base currency");
		}
	}

	private void ensureCurrenciesToExchangeIsNotEmpty(Set<CurrencyType> currenciesToExchange) {
		if (isNull(currenciesToExchange) || currenciesToExchange.isEmpty()) {
			throw new IllegalArgumentException("Currencies to exchange cannot be null or empty");
		}
	}
}
