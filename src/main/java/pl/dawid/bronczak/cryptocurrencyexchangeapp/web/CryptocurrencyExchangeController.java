package pl.dawid.bronczak.cryptocurrencyexchangeapp.web;

import org.springframework.web.bind.annotation.*;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CryptocurrencyExchangeService;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CurrencyType;

import javax.validation.Valid;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class CryptocurrencyExchangeController {

	private final CryptocurrencyExchangeService cryptocurrencyExchangeService;

	CryptocurrencyExchangeController(CryptocurrencyExchangeService cryptocurrencyExchangeService) {
		this.cryptocurrencyExchangeService = cryptocurrencyExchangeService;
	}

	@GetMapping(value = "/currencies/{currencyType}", produces = APPLICATION_JSON_VALUE)
	CryptocurrencyExchangeRateResponse getCryptocurrencyExchangeRate(
			@PathVariable("currencyType") CurrencyType baseCurrency,
			@RequestParam(value = "filter[]", required = false) Set<CurrencyType> currenciesToExchange) {
		return cryptocurrencyExchangeService.getExchangeRate(baseCurrency, currenciesToExchange);
	}

	@PostMapping(value = "/currencies/exchange", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	CryptocurrencyExchangeForecastResponse getCryptocurrencyExchangeForecast(
			@RequestBody @Valid CryptocurrencyExchangeForecastRequest cryptocurrencyExchangeForecastRequest) {
		return cryptocurrencyExchangeService.calculateExchangeForecast(
				cryptocurrencyExchangeForecastRequest.getBaseCurrency(),
				cryptocurrencyExchangeForecastRequest.getCurrenciesToExchange(),
				cryptocurrencyExchangeForecastRequest.getAmount());
	}
}
