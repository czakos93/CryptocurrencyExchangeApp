package pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class CurrencyType {

	private final String value;

	public CurrencyType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getStringValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CurrencyType that = (CurrencyType) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "CurrencyType{" +
				"value='" + value + '\'' +
				'}';
	}
}
