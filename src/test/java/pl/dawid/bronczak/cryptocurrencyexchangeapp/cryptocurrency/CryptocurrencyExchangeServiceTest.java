package pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.CryptocurrencyApiClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastRequest;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastResponse;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeRateResponse;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.dawid.bronczak.cryptocurrencyexchangeapp.TestConstants.*;

class CryptocurrencyExchangeServiceTest {

	private static final BigDecimal EXPECTED_EXCHANGE_RESULT_FOR_CBA = BigDecimal.valueOf(1543.4532);
	private static final BigDecimal EXPECTED_EXCHANGE_RESULT_FOR_BCA = BigDecimal.valueOf(6657.38976);
	@Mock
	private CryptocurrencyApiClient mockedCryptocurrencyApiClient;
	private CryptocurrencyExchangeService cryptocurrencyExchangeService;

	@BeforeEach
	void setUp() {
		initMocks(this);
		cryptocurrencyExchangeService = new CryptocurrencyExchangeService(mockedCryptocurrencyApiClient, FEE_PERCENT);

		when(mockedCryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(eq(ABC), eq(BCA)))
				.thenReturn(BCA_EXCHANGE_RATE);

		when(mockedCryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(eq(ABC), eq(CBA)))
				.thenReturn(CBA_EXCHANGE_RATE);

		when(mockedCryptocurrencyApiClient.fetchCryptocurrencyExchangeRateForAllCryptocurrencies(any(CurrencyType.class)))
				.thenReturn(ImmutableMap.of(
						BCA, BCA_EXCHANGE_RATE,
						CBA, CBA_EXCHANGE_RATE
				));
	}

	@AfterEach
	void resetMocks() {
		reset(mockedCryptocurrencyApiClient);
	}

	@Test
	void returnsExchangeRatesFromBaseCurrencyToAllCurrenciesWhenCurrencyFilterIsEmpty() {
		CryptocurrencyExchangeRateResponse response = cryptocurrencyExchangeService.getExchangeRate(ABC, emptySet());

		assertThat(response.getSource())
				.isEqualTo(ABC);
		assertThat(response.getRates())
				.hasSize(2)
				.contains(
						entry(BCA, BCA_EXCHANGE_RATE),
						entry(CBA, CBA_EXCHANGE_RATE)
				);
	}

	@Test
	void returnsExchangeRatesFromBaseCurrencyToAllCurrenciesWhenCurrencyFilterIsNull() {
		CryptocurrencyExchangeRateResponse response = cryptocurrencyExchangeService.getExchangeRate(ABC, null);

		assertThat(response.getSource())
				.isEqualTo(ABC);
		assertThat(response.getRates())
				.hasSize(2)
				.contains(
						entry(BCA, BCA_EXCHANGE_RATE),
						entry(CBA, CBA_EXCHANGE_RATE)
				);
	}

	@Test
	void returnsExchangeRateFromBasedCurrencyToOnlyFilteredCurrencies() {
		CryptocurrencyExchangeRateResponse response = cryptocurrencyExchangeService.getExchangeRate(ABC, singleton(BCA));

		assertThat(response.getSource())
				.isEqualTo(ABC);
		assertThat(response.getRates())
				.containsEntry(BCA, BCA_EXCHANGE_RATE);
	}

	@Test
	void returnsExchangeForecastForAllRequestedCurrencies() {
		CryptocurrencyExchangeForecastRequest cryptocurrencyExchangeForecastRequest = new CryptocurrencyExchangeForecastRequest(ABC, newHashSet(CBA, BCA), AMOUNT);

		CryptocurrencyExchangeForecastResponse response = cryptocurrencyExchangeService.calculateExchangeForecast(cryptocurrencyExchangeForecastRequest);

		assertThat(response.getBaseCurrency())
				.isEqualTo(ABC);

		assertThat(response.getExchangeResults())
				.hasSize(2)
				.contains(
						entry(CBA, new ExchangeDetails(CBA_EXCHANGE_RATE, AMOUNT, EXPECTED_EXCHANGE_RESULT_FOR_CBA, EXCHANGE_FEE)),
						entry(BCA, new ExchangeDetails(BCA_EXCHANGE_RATE, AMOUNT, EXPECTED_EXCHANGE_RESULT_FOR_BCA, EXCHANGE_FEE)));
	}

	@Test
	void throwsIllegalArgumentExceptionWhenFilteredCurrenciesContainBaseCurrency() {
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> cryptocurrencyExchangeService.getExchangeRate(ABC, newHashSet(BCA, CBA, ABC)));

		assertThat(illegalArgumentException)
				.hasMessage("Currencies to exchange contain base currency");

		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsIllegalArgumentExceptionWhenCurrenciesToExchangeIsNull() {
		CryptocurrencyExchangeForecastRequest requestWithNullCurrenciesToExchange = new CryptocurrencyExchangeForecastRequest(ABC, null, AMOUNT);
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> cryptocurrencyExchangeService.calculateExchangeForecast(requestWithNullCurrenciesToExchange));

		assertThat(illegalArgumentException)
				.hasMessage("Currencies to exchange cannot be null or empty");

		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsIllegalArgumentExceptionWhenCurrenciesToExchangeIsEmpty() {
		CryptocurrencyExchangeForecastRequest requestWithEmptyCurrenciesToExchange = new CryptocurrencyExchangeForecastRequest(ABC, emptySet(), AMOUNT);

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> cryptocurrencyExchangeService.calculateExchangeForecast(requestWithEmptyCurrenciesToExchange));

		assertThat(illegalArgumentException)
				.hasMessage("Currencies to exchange cannot be null or empty");

		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}
}