package pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Set;

public class CoinApiExchangeRateForAllCurrenciesResponse {

	private final Set<CoinApiExchangeRateDetails> ratesDetails;

	@JsonCreator
	public CoinApiExchangeRateForAllCurrenciesResponse(
			@JsonProperty("rates") Set<CoinApiExchangeRateDetails> ratesDetails) {
		this.ratesDetails = ratesDetails;
	}

	public Set<CoinApiExchangeRateDetails> getRatesDetails() {
		return ratesDetails;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CoinApiExchangeRateForAllCurrenciesResponse response = (CoinApiExchangeRateForAllCurrenciesResponse) o;
		return Objects.equals(ratesDetails, response.ratesDetails);
	}

	@Override
	public String toString() {
		return "CoinApiExchangeRateForAllCurrenciesResponse{" +
				"ratesDetails=" + ratesDetails +
				'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(ratesDetails);
	}
}
