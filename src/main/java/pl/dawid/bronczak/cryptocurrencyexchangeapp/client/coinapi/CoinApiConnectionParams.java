package pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.net.URI;

@ConfigurationProperties(prefix = "api-clients.coin-api")
@ConstructorBinding
public class CoinApiConnectionParams {

	private final URI hostAddress;
	private final String apiKey;

	public CoinApiConnectionParams(URI hostAddress, String apiKey) {
		this.hostAddress = hostAddress;
		this.apiKey = apiKey;
	}

	public URI getHostAddress() {
		return hostAddress;
	}

	public String getApiKey() {
		return apiKey;
	}
}
