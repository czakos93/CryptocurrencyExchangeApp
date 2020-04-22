package pl.dawid.bronczak.cryptocurrencyexchangeapp.client.coinapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.math.BigDecimal;
import java.util.Objects;

public class CoinApiExchangeRateDetails {

	private final CurrencyType currencyType;
	private final BigDecimal exchangeRate;

	@JsonCreator
	public CoinApiExchangeRateDetails(
			@JsonProperty("asset_id_quote") CurrencyType currencyType,
			@JsonProperty("rate") BigDecimal exchangeRate) {
		this.currencyType = currencyType;
		this.exchangeRate = exchangeRate;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CoinApiExchangeRateDetails that = (CoinApiExchangeRateDetails) o;
		return Objects.equals(currencyType, that.currencyType) &&
				Objects.equals(exchangeRate, that.exchangeRate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(currencyType, exchangeRate);
	}

	@Override
	public String toString() {
		return "CoinApiExchangeRateDetails{" +
				"currencyType=" + currencyType +
				", exchangeRate=" + exchangeRate +
				'}';
	}
}
