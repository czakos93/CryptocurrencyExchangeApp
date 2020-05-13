package pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.internal.verification.Times;
import org.mockito.verification.VerificationMode;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.CryptocurrencyApiClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastRequest;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeForecastResponse;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.web.CryptocurrencyExchangeRateResponse;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

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
		cryptocurrencyExchangeService = new CryptocurrencyExchangeService(
				mockedCryptocurrencyApiClient,
				FEE_PERCENT,
				THREAD_AMOUNT);

		when(mockedCryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(eq(ABC), eq(BCA)))
				.thenReturn(BCA_EXCHANGE_RATE);

		when(mockedCryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(eq(ABC), eq(CBA)))
				.thenReturn(CBA_EXCHANGE_RATE);

		when(mockedCryptocurrencyApiClient.fetchExchangeRatesForAllCurrencies(any(CurrencyType.class)))
				.thenReturn(ImmutableMap.of(
						BCA, BCA_EXCHANGE_RATE,
						CBA, CBA_EXCHANGE_RATE
				));

		when(mockedCryptocurrencyApiClient.fetchCryptocurrencyExchangeRate(eq(ABC), eq(CAB)))
				.thenThrow(new RuntimeException("some error"));
	}

	@AfterEach
	void resetMocks() {
		reset(mockedCryptocurrencyApiClient);
	}

	//getExchangeRate() method tests
	@Test
	void returnsExchangeRatesFromBaseCurrencyToAllCurrenciesWhenCurrenciesToCheckExchangeRateIsEmpty() {
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
	void returnsExchangeRatesFromBaseCurrencyToAllCurrenciesWhenCurrenciesToCheckExchangeRateIsNull() {
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
				.hasSize(1)
				.containsEntry(BCA, BCA_EXCHANGE_RATE);
	}

	@Test
	void skipsExchangeRatesFromBaseCurrencyToBaseCurrency() {
		CryptocurrencyExchangeRateResponse response = cryptocurrencyExchangeService.getExchangeRate(ABC, newHashSet(BCA, ABC));

		assertThat(response.getSource())
				.isEqualTo(ABC);
		assertThat(response.getRates())
				.hasSize(1)
				.containsEntry(BCA, BCA_EXCHANGE_RATE);

		verify(mockedCryptocurrencyApiClient, never()).fetchCryptocurrencyExchangeRate(ABC, ABC);
	}

	@Test
	void throwsNullPointerExceptionWhenCallGetExchangeRateAndBaseCurrencyIsNull() {
		NullPointerException nullPointerException = assertThrows(NullPointerException.class,
				() -> cryptocurrencyExchangeService.getExchangeRate(null, newHashSet(BCA, CBA, ABC)));

		assertThat(nullPointerException)
				.hasMessage("base currency cannot be null");

		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsIllegalStateExceptionWhenExceptionWillBeThrownDuringFetchingExchangeRates() {
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> cryptocurrencyExchangeService.getExchangeRate(ABC, newHashSet(BCA, CAB)));

		assertThat(illegalStateException)
				.hasCause(new ExecutionException("java.lang.RuntimeException: some error", new RuntimeException()))
				.hasMessage("Cannot fetch exchange rates");
	}

	//calculateExchangeForecast() method tests
	@Test
	void returnsExchangeForecastForAllRequestedCurrencies() {
		CryptocurrencyExchangeForecastResponse response = cryptocurrencyExchangeService.calculateExchangeForecast(ABC, newHashSet(CBA, BCA), AMOUNT);

		assertThat(response.getBaseCurrency())
				.isEqualTo(ABC);

		assertThat(response.getExchangeResults())
				.hasSize(2)
				.contains(
						entry(CBA, new ExchangeDetails(CBA_EXCHANGE_RATE, AMOUNT, EXPECTED_EXCHANGE_RESULT_FOR_CBA, EXCHANGE_FEE)),
						entry(BCA, new ExchangeDetails(BCA_EXCHANGE_RATE, AMOUNT, EXPECTED_EXCHANGE_RESULT_FOR_BCA, EXCHANGE_FEE)));
	}

	@Test
	void skipsExchangeForecastFromBaseCurrencyToBaseCurrency() {
		CryptocurrencyExchangeForecastResponse response = cryptocurrencyExchangeService.calculateExchangeForecast(ABC, newHashSet(ABC, CBA, BCA), AMOUNT);

		assertThat(response.getBaseCurrency())
				.isEqualTo(ABC);

		assertThat(response.getExchangeResults())
				.hasSize(2)
				.contains(
						entry(CBA, new ExchangeDetails(CBA_EXCHANGE_RATE, AMOUNT, EXPECTED_EXCHANGE_RESULT_FOR_CBA, EXCHANGE_FEE)),
						entry(BCA, new ExchangeDetails(BCA_EXCHANGE_RATE, AMOUNT, EXPECTED_EXCHANGE_RESULT_FOR_BCA, EXCHANGE_FEE)));

		verify(mockedCryptocurrencyApiClient, never()).fetchCryptocurrencyExchangeRate(ABC, ABC);
	}

	@Test
	void returnsEmptyExchangeForecastWhenCurrenciesToExchangeIsEmpty() {
		CryptocurrencyExchangeForecastResponse response = cryptocurrencyExchangeService.calculateExchangeForecast(ABC, emptySet(), AMOUNT);

		assertThat(response.getBaseCurrency())
				.isEqualTo(ABC);

		assertThat(response.getExchangeResults())
				.isEmpty();
		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsNullPointerExceptionWhenCallCalculateExchangeForecastAndBaseCurrencyIsNull() {
		NullPointerException nullPointerException = assertThrows(NullPointerException.class,
				() -> cryptocurrencyExchangeService.calculateExchangeForecast(null, newHashSet(ABC), AMOUNT));

		assertThat(nullPointerException)
				.hasMessage("base currency cannot be null");
		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsNullPointerExceptionWhenCallCalculateExchangeForecastAndCurrenciesToExchangeIsNull() {
		NullPointerException nullPointerException = assertThrows(NullPointerException.class,
				() -> cryptocurrencyExchangeService.calculateExchangeForecast(ABC, null, AMOUNT));

		assertThat(nullPointerException)
				.hasMessage("currencies to exchange cannot be null");
		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsNullPointerExceptionWhenCallCalculateExchangeForecastAndAmountIsNull() {
		NullPointerException nullPointerException = assertThrows(NullPointerException.class,
				() -> cryptocurrencyExchangeService.calculateExchangeForecast(ABC, newHashSet(ABC), null));

		assertThat(nullPointerException)
				.hasMessage("amount cannot be null");
		verifyNoInteractions(mockedCryptocurrencyApiClient);
	}

	@Test
	void throwsIllegalStateExceptionWhenExceptionWillBeThrownDuringCalculatingExchangeRates() {
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> cryptocurrencyExchangeService.calculateExchangeForecast(ABC, newHashSet(BCA, CAB), AMOUNT));

		assertThat(illegalStateException)
				.hasCause(new ExecutionException("java.lang.RuntimeException: some error", new RuntimeException()))
				.hasMessage("Cannot calculate exchange forecasts");
	}
}