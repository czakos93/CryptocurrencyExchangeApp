package pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.CryptocurrencyApiClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpMethod.GET;

public class CoinApiRestClient implements CryptocurrencyApiClient {

	private static final String CURRENCY_EXCHANGE_RATE_ENDPOINT = "/v1/exchangerate/";
	private static final String X_COIN_API_KEY_HEADER = "X-CoinAPI-Key";
	private final HttpEntity requestHeaders;

	private final RestTemplate restTemplate;
	private final CoinApiConnectionParams connectionParams;

	public CoinApiRestClient(RestTemplate restTemplate, CoinApiConnectionParams connectionParams) {
		this.restTemplate = restTemplate;
		this.connectionParams = connectionParams;
		this.requestHeaders = prepareApiKeyHeader();
	}

	@Override
	public BigDecimal fetchCryptocurrencyExchangeRate(CurrencyType baseCurrency, CurrencyType currencyToCheckExchangeRate) {
		return new BigDecimal(restTemplate.exchange(prepareRequestURI(baseCurrency, currencyToCheckExchangeRate), GET, requestHeaders, JsonNode.class)
				.getBody()
				.findValue("rate")
				.asText());
	}

	@Override
	public Map<CurrencyType, BigDecimal> fetchExchangeRatesForAllCurrencies(CurrencyType baseCurrency) {

		return restTemplate.exchange(prepareRequestURI(baseCurrency), GET, requestHeaders, CoinApiExchangeRateForAllCurrenciesResponse.class)
				.getBody()
				.getRatesDetails().stream()
				.collect(toMap(
						CoinApiExchangeRateDetails::getCurrencyType,
						CoinApiExchangeRateDetails::getExchangeRate));
	}

	private URI prepareRequestURI(CurrencyType baseCurrency) {
		return UriComponentsBuilder.fromUri(connectionParams.getHostAddress())
				.pathSegment(CURRENCY_EXCHANGE_RATE_ENDPOINT)
				.pathSegment(baseCurrency.getStringValue())
				.build()
				.toUri()
				.normalize();
	}

	private URI prepareRequestURI(CurrencyType baseCurrency, CurrencyType currencyToCheckExchangeRate) {
		return UriComponentsBuilder.fromUri(prepareRequestURI(baseCurrency))
				.pathSegment(currencyToCheckExchangeRate.getStringValue())
				.build()
				.toUri()
				.normalize();
	}

	private HttpEntity prepareApiKeyHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(X_COIN_API_KEY_HEADER, connectionParams.getApiKey());
		return new HttpEntity(httpHeaders);
	}
}
