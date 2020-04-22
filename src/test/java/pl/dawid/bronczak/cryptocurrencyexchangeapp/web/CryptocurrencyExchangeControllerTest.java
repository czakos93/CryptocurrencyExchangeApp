package pl.dawid.bronczak.cryptocurrencyexchangeapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.CryptocurrencyExchangeService;
import pl.dawid.bronczak.cryptocurrencyexchangeapp.cryptocurrency.ExchangeDetails;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dawid.bronczak.cryptocurrencyexchangeapp.TestConstants.*;

class CryptocurrencyExchangeControllerTest {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Mock
	private CryptocurrencyExchangeService cryptocurrencyExchangeService;

	private CryptocurrencyExchangeController cryptocurrencyExchangeController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUpMocks() {
		initMocks(this);
		cryptocurrencyExchangeController = new CryptocurrencyExchangeController(cryptocurrencyExchangeService);
		mockMvc = MockMvcBuilders.standaloneSetup(cryptocurrencyExchangeController).build();
	}

	@Test
	void shouldReturnsStatusOkAndExchangeRateWhenCurrenciesFilterIsDefined() throws Exception {
		when(cryptocurrencyExchangeService.getExchangeRate(eq(ABC), eq(newHashSet(CBA, BCA))))
				.thenReturn(new CryptocurrencyExchangeRateResponse(ABC, ImmutableMap.of(CBA, BigDecimal.valueOf(123.4567890),
						BCA, BigDecimal.valueOf(98.7654321))));

		mockMvc.perform(
				get("/currencies/ABC?filter=CBA&filter=BCA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*]", hasSize(2)))
				.andExpect(jsonPath("$.source").value(ABC.getStringValue()))
				.andExpect(jsonPath("$.rates[*]", hasSize(2)))
				.andExpect(jsonPath("$.rates.CBA").value(123.4567890))
				.andExpect(jsonPath("$.rates.BCA").value(98.7654321));
	}

	@Test
	void returnsStatusOkAndExchangeForecast() throws Exception {
		CryptocurrencyExchangeForecastRequest cryptocurrencyExchangeForecastRequest = new CryptocurrencyExchangeForecastRequest(ABC, newHashSet(CBA, BCA), new BigDecimal(123));

		when(cryptocurrencyExchangeService.calculateExchangeForecast(eq(cryptocurrencyExchangeForecastRequest)))
				.thenReturn(new CryptocurrencyExchangeForecastResponse(
						ABC,
						ImmutableMap.of(
								BCA, new ExchangeDetails(BigDecimal.valueOf(123.4567890), BigDecimal.valueOf(10), BigDecimal.valueOf(98.7654321), BigDecimal.valueOf(12345.6789)),
								CBA, new ExchangeDetails(BigDecimal.valueOf(1.23456789), BigDecimal.valueOf(20), BigDecimal.valueOf(1234567.89), BigDecimal.valueOf(12.3456789)))));


		mockMvc.perform(post("/currencies/exchange")
				.content(OBJECT_MAPPER.writeValueAsString(cryptocurrencyExchangeForecastRequest))
				.contentType(APPLICATION_JSON_VALUE)
				.accept(APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*]", hasSize(3)))
				.andExpect(jsonPath("$.from").value(ABC.getStringValue()))
				.andExpect(jsonPath("$.BCA[*]", hasSize(4)))
				.andExpect(jsonPath("$.BCA.rate").value(123.4567890))
				.andExpect(jsonPath("$.BCA.amount").value(10))
				.andExpect(jsonPath("$.BCA.result").value(98.7654321))
				.andExpect(jsonPath("$.BCA.fee").value(12345.6789))
				.andExpect(jsonPath("$.CBA[*]", hasSize(4)))
				.andExpect(jsonPath("$.CBA.rate").value(1.23456789))
				.andExpect(jsonPath("$.CBA.amount").value(20))
				.andExpect(jsonPath("$.CBA.result").value(1234567.89))
				.andExpect(jsonPath("$.CBA.fee").value(12.3456789));

	}

	ExchangeDetails exchangeDetails() {
		return new ExchangeDetails(BigDecimal.valueOf(123.4567890), BigDecimal.valueOf(10), BigDecimal.valueOf(98.7654321), BigDecimal.valueOf(12345.6789));
	}

}