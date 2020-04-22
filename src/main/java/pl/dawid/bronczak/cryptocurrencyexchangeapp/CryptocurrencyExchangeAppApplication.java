package pl.dawid.bronczak.cryptocurrencyexchangeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CryptocurrencyExchangeAppConfig.class)
public class CryptocurrencyExchangeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptocurrencyExchangeAppApplication.class, args);
	}

}
