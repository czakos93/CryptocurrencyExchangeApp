package pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency;

import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.CryptocurrencyApiClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastRequest;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastResponse;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeRateResponse;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.isNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toMap;

public class CryptocurrencyExchangeService {

    private final CryptocurrencyApiClient cryptocurrencyApiClient;
    private final float feePercent;
    private final ExecutorService executorService;

    public CryptocurrencyExchangeService(
            CryptocurrencyApiClient cryptocurrencyApiClient,
            int feePercent,
            int numberOfThreads) {
        this.cryptocurrencyApiClient = cryptocurrencyApiClient;
        this.feePercent = feePercent;
        this.executorService = newFixedThreadPool(numberOfThreads);
    }

    public CryptocurrencyExchangeRateResponse getExchangeRate(CurrencyType baseCurrency, Set<CurrencyType> currenciesToCheckExchangeRate) {
        checkNotNull(baseCurrency, "base currency cannot be null");

        Map<CurrencyType, BigDecimal> exchangeRates;

        if (isNull(currenciesToCheckExchangeRate) || currenciesToCheckExchangeRate.isEmpty()) {
            exchangeRates = cryptocurrencyApiClient.fetchExchangeRatesForAllCurrencies(baseCurrency);
        } else {
            exchangeRates = tryToFetchExchangeRate(baseCurrency, currenciesToCheckExchangeRate);
        }

        return new CryptocurrencyExchangeRateResponse(baseCurrency, exchangeRates);
    }

    public CryptocurrencyExchangeForecastResponse calculateExchangeForecast(
            CurrencyType baseCurrency,
            Set<CurrencyType> currenciesToExchange,
            BigDecimal amount) {
        checkNotNull(baseCurrency, "base currency cannot be null");
        checkNotNull(currenciesToExchange, "currencies to exchange cannot be null");
        checkNotNull(amount, "amount cannot be null");

        BigDecimal exchangeFee = calculateFee(amount);

        try {
            Map<CurrencyType, ExchangeDetails> exchangeForecasts = executorService.submit(() ->
                    currenciesToExchange.parallelStream()
                            .filter(currencyToCheckExchangeRate -> !currencyToCheckExchangeRate.equals(baseCurrency))
                            .map(fetchCurrencyExchangeRateFrom(baseCurrency))
                            .map(prepareExchangeDetails(amount, exchangeFee))
                            .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)))
                    .get();

            return new CryptocurrencyExchangeForecastResponse(
                    baseCurrency,
                    exchangeForecasts);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot calculate exchange forecasts", e);
        }
    }

    private BigDecimal calculateFee(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(feePercent))
                .divide(BigDecimal.valueOf(100));
    }

    private Function<SimpleEntry<CurrencyType, BigDecimal>, SimpleEntry<CurrencyType, ExchangeDetails>> prepareExchangeDetails(BigDecimal amount, BigDecimal fee) {
        return currencyExchangeRate -> new SimpleEntry<>(currencyExchangeRate.getKey(),
                new ExchangeDetails(
                        currencyExchangeRate.getValue(),
                        amount,
                        currencyExchangeRate.getValue().multiply(amount), fee));
    }

    private Function<CurrencyType, SimpleEntry<CurrencyType, BigDecimal>> fetchCurrencyExchangeRateFrom(CurrencyType baseCurrency) {
        return currencyToCheckExchangeRate -> new SimpleEntry<>(currencyToCheckExchangeRate, cryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(baseCurrency, currencyToCheckExchangeRate));
    }

    private Map<CurrencyType, BigDecimal> tryToFetchExchangeRate(CurrencyType baseCurrency, Set<CurrencyType> currenciesToCheckExchangeRate) {
        try {
            return executorService.submit(() -> currenciesToCheckExchangeRate.parallelStream()
                    .filter(currencyToCheckExchangeRate -> !currencyToCheckExchangeRate.equals(baseCurrency))
                    .map(fetchCurrencyExchangeRateFrom(baseCurrency))
                    .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)))
                    .get();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot fetch exchange rates", e);
        }
    }
}
