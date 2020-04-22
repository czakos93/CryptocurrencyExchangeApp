package pl.dawid.bronczak.cryptocurrencyexchangeapp.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.ExchangeDetails;

import java.util.Map;
import java.util.Objects;

public class CryptocurrencyExchangeForecastResponse {

	private final CurrencyType from;
	private final Map<CurrencyType, ExchangeDetails> exchangeResults;

	public CryptocurrencyExchangeForecastResponse(CurrencyType from, Map<CurrencyType, ExchangeDetails> exchangeResults) {
		this.from = from;
		this.exchangeResults = exchangeResults;
	}

	@JsonGetter("from")
	public CurrencyType getBaseCurrency() {
		return from;
	}

	@JsonAnyGetter
	public Map<CurrencyType, ExchangeDetails> getExchangeResults() {
		return exchangeResults;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CryptocurrencyExchangeForecastResponse that = (CryptocurrencyExchangeForecastResponse) o;
		return Objects.equals(from, that.from) &&
				Objects.equals(exchangeResults, that.exchangeResults);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, exchangeResults);
	}

	@Override
	public String toString() {
		return "CryptocurrencyExchangeForecastResponse{" +
				"from=" + from +
				", exchangeResults=" + exchangeResults +
				'}';
	}
}
