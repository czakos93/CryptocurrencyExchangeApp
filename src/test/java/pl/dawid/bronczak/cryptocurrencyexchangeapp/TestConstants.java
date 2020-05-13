package pl.dawid.bronczak.cryptocurrencyexchangeapp;

import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import java.math.BigDecimal;

public class TestConstants {

	public static final CurrencyType ABC = new CurrencyType("ABC");
	public static final CurrencyType CBA = new CurrencyType("CBA");
	public static final CurrencyType BCA = new CurrencyType("BCA");
	public static final CurrencyType CAB = new CurrencyType("CAB");

	public static final int FEE_PERCENT = 7;
	public static final int THREAD_AMOUNT = 2;

	public static final BigDecimal CBA_EXCHANGE_RATE = BigDecimal.valueOf(12.5484);
	public static final BigDecimal BCA_EXCHANGE_RATE = BigDecimal.valueOf(54.12512);
	public static final BigDecimal AMOUNT = new BigDecimal(123);
	public static final BigDecimal EXCHANGE_FEE = BigDecimal.valueOf(8.61);
}
