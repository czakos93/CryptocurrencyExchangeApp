package pl.dawid.bronczak.cryptocurrencyexchangeapp.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

public class CryptocurrencyExchangeForecastRequest {

	private final CurrencyType baseCurrency;
	private final Set<CurrencyType> currenciesToExchange;
	private final BigDecimal amount;

	@JsonCreator
	public CryptocurrencyExchangeForecastRequest(
			@JsonProperty("from") CurrencyType baseCurrency,
			@JsonProperty("to") Set<CurrencyType> currenciesToExchange,
			@JsonProperty("amount") BigDecimal amount) {
		this.baseCurrency = baseCurrency;
		this.currenciesToExchange = currenciesToExchange;
		this.amount = amount;
	}

	@JsonGetter("from")
	public CurrencyType getBaseCurrency() {
		return baseCurrency;
	}

	@JsonGetter("to")
	public Set<CurrencyType> getCurrenciesToExchange() {
		return currenciesToExchange;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CryptocurrencyExchangeForecastRequest that = (CryptocurrencyExchangeForecastRequest) o;
		return Objects.equals(baseCurrency, that.baseCurrency) &&
				Objects.equals(currenciesToExchange, that.currenciesToExchange) &&
				Objects.equals(amount, that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseCurrency, currenciesToExchange, amount);
	}

	@Override
	public String toString() {
		return "CryptocurrencyExchangeForecastRequest{" +
				"baseCurrency=" + baseCurrency +
				", currenciesToExchange=" + currenciesToExchange +
				", amount=" + amount +
				'}';
	}
}
