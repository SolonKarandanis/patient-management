package com.pm.camelintegration.components.rest;

import com.pm.camelintegration.dto.WeatherDataProvider;
import com.pm.camelintegration.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Component
public class RestDsl extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;

    public RestDsl(WeatherDataProvider weatherDataProvider) {
        this.weatherDataProvider = weatherDataProvider;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.auto);

        // http://localhost:4007/camel-integration/camel/weather/London
        rest()
                .consumes(MediaType.APPLICATION_JSON_VALUE).produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/weather/{city}").outType(WeatherDto.class).to("direct:get-weather-data")
                .post("/weather").type(WeatherDto.class).to("direct:save-weather-data");

        from("direct:get-weather-data").process(this::getWeatherData);

        from("direct:save-weather-data")
                .process(this::saveWeatherDataAndSetToExchange)
                .wireTap("direct:write-to-rabbit");

//        from("direct:write-to-rabbit")
//                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
//                .toF(RABBIT_URI, "weather-event", "weather-event");
    }

    private void saveWeatherDataAndSetToExchange(Exchange exchange) {
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        weatherDataProvider.setCurrentWeather(dto);
    }

    private void getWeatherData(Exchange exchange) {
        String city = exchange.getMessage().getHeader("city", String.class);
        WeatherDto currentWeather = weatherDataProvider.getCurrentWeather(city);

        if (Objects.nonNull(currentWeather)) {
            Message message = new DefaultMessage(exchange.getContext());
            message.setBody(currentWeather);
            exchange.setMessage(message);
        } else {
            exchange.getMessage().setHeader(HTTP_RESPONSE_CODE, NOT_FOUND.value());
        }
    }
}
