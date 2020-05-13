package pl.dawid.bronczak.cryptocurrencyexchangeapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.CryptocurrencyApiClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi.CoinApiConnectionParams;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi.CoinApiRestClient;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CryptocurrencyExchangeService;

@EnableConfigurationProperties(CoinApiConnectionParams.class)
public class CryptocurrencyExchangeAppConfig {

	@Value("${exchangeFeeInPercent}")
	private int feePercent;

	@Value("${threadsPoolAmount}")
	private int threadPoolAmount;

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	CryptocurrencyApiClient coinApiClient(RestTemplate restTemplate, CoinApiConnectionParams coinApiConnectionParams) {
		return new CoinApiRestClient(restTemplate, coinApiConnectionParams);
	}

	@Bean
	CryptocurrencyExchangeService cryptocurrencyExchangeService(CryptocurrencyApiClient cryptocurrencyApiClient) {
		return new CryptocurrencyExchangeService(cryptocurrencyApiClient, feePercent, threadPoolAmount);
	}
}
