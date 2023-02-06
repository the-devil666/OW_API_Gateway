package com.example.gateway.apigateway;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="weather-service", path = "api/v1", contextId = "v1")
interface WeatherServerClientV1 {
	@RequestMapping(method = RequestMethod.GET, value = "weather", consumes = "application/json")
	OpenWeatherApiResponse getWeatherByZip(@RequestParam("zip") String zip);
}

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@RestController("gateway-api")
	@RequestMapping()
	public class WeatherGatewayService {

		Logger logger = LoggerFactory.getLogger(WeatherGatewayService.class);

		private final WeatherServerClientV1 weatherClientV1;

		public WeatherGatewayService(WeatherServerClientV1 weatherClientV1) {
			this.weatherClientV1 = weatherClientV1;
		}

		@GetMapping("/temperature/zip/{zip}")
		String getTempByZip(@PathVariable("zip") String zip) {
			logger.info("Getting weather for zip = " + zip);
			OpenWeatherApiResponse weatherApiResponse = weatherClientV1.getWeatherByZip(zip);
			logger.info(weatherApiResponse.toString());
			return weatherApiResponse.getMain().getTemp().toString()+"°F, feels like " + weatherApiResponse.getMain().getFeels_like().toString()+"°F";
		}

	}

}