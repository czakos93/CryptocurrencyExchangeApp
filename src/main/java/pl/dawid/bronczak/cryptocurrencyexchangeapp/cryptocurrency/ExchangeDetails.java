package pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeDetails {

	private final BigDecimal rate;
	private final BigDecimal amount;
	private final BigDecimal result;
	private final BigDecimal fee;

	public ExchangeDetails(
			BigDecimal rate,
			BigDecimal amount,
			BigDecimal result,
			BigDecimal fee) {
		this.rate = rate;
		this.amount = amount;
		this.result = result;
		this.fee = fee;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getResult() {
		return result;
	}

	public BigDecimal getFee() {
		return fee;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExchangeDetails that = (ExchangeDetails) o;
		return Objects.equals(rate, that.rate) &&
				Objects.equals(amount, that.amount) &&
				Objects.equals(result, that.result) &&
				Objects.equals(fee, that.fee);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rate, amount, result, fee);
	}


	@Override
	public String toString() {
		return "ExchangeDetails{" +
				"rate=" + rate +
				", amount=" + amount +
				", result=" + result +
				", fee=" + fee +
				'}';
	}
}
