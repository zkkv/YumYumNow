package nl.tudelft.sem.yumyumnow.delivery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;


@Configuration
public class Config {
    /**
     * Gets the clock
     * @return the clock
     */
    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
