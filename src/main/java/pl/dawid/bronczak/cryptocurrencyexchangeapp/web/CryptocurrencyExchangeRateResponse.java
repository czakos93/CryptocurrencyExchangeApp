package pl.dawid.bronczak.cryptocurrencyexchangeapp.web;

import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class CryptocurrencyExchangeRateResponse {

	private final CurrencyType source;
	private final Map<CurrencyType, BigDecimal> rates;

	public CryptocurrencyExchangeRateResponse(CurrencyType source, Map<CurrencyType, BigDecimal> rates) {
		this.source = source;
		this.rates = rates;
	}

	public CurrencyType getSource() {
		return source;
	}

	public Map<CurrencyType, BigDecimal> getRates() {
		return rates;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CryptocurrencyExchangeRateResponse response = (CryptocurrencyExchangeRateResponse) o;
		return Objects.equals(source, response.source) &&
				Objects.equals(rates, response.rates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, rates);
	}

	@Override
	public String toString() {
		return "CryptocurrencyExchangeRateResponse{" +
				"source=" + source +
				", rates=" + rates +
				'}';
	}
}
