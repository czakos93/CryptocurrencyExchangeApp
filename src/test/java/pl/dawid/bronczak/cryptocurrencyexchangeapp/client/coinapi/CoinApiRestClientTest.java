package pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static pl.dawid.bronczak.cryptocurrencyexchangeapp.TestConstants.*;

class CoinApiRestClientTest {

	private MockRestServiceServer mockedCoinApiServer;
	private CoinApiRestClient coinApiRestClient;

	private static final byte[] RESPONSE_BODY_FOR_SINGLE_CURRENCY = readFileAsByteArray("src/test/resources/exchangeRateResponseBodyForSingleCurrency.json");
	private static final byte[] RESPONSE_BODY_FOR_ALL_CURRENCIES = readFileAsByteArray("src/test/resources/exchangeRateResponseBodyForAllCurrencies.json");

	@BeforeEach
	void setUpClient() {
		RestTemplate restTemplate = new RestTemplate();
		CoinApiConnectionParams connectionParams = new CoinApiConnectionParams(URI.create("http://dummy-host/"), "dummy-key");
		coinApiRestClient = new CoinApiRestClient(restTemplate, connectionParams);
		mockedCoinApiServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	void shouldSendRequestForExchangeRateForAllCryptocurrencies() {
		mockedCoinApiServer.expect(requestTo("http://dummy-host/v1/exchangerate/ABC"))
				.andExpect(method(GET))
				.andExpect(header("X-CoinAPI-Key", "dummy-key"))
				.andRespond(withStatus(OK)
						.contentType(APPLICATION_JSON)
						.body(RESPONSE_BODY_FOR_ALL_CURRENCIES));

		Map<CurrencyType, BigDecimal> exchangeRates = coinApiRestClient.fetchCryptocurrencyExchangeRateForAllCryptocurrencies(ABC);

		assertThat(exchangeRates)
				.hasSize(2)
				.contains(
						entry(CBA, BigDecimal.valueOf(2782.52550805)),
						entry(BCA, BigDecimal.valueOf(3258.88754177)));

		mockedCoinApiServer.verify();
	}

	@Test
	void shouldSendRequestForExchangeRateForSingleCryptocurrency() {
		mockedCoinApiServer.expect(requestTo("http://dummy-host/v1/exchangerate/ABC/CBA"))
				.andExpect(method(GET))
				.andExpect(header("X-CoinAPI-Key", "dummy-key"))
				.andRespond(withStatus(OK)
						.contentType(APPLICATION_JSON)
						.body(RESPONSE_BODY_FOR_SINGLE_CURRENCY));

		BigDecimal exchangeRate = coinApiRestClient.fetchCryptocurrencyExchangeRate(ABC, CBA);

		assertThat(exchangeRate)
				.isEqualTo("3260.35143212");

		mockedCoinApiServer.verify();
	}

	private static byte[] readFileAsByteArray(String path) {
		try {
			return Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read file", e);
		}
	}
}