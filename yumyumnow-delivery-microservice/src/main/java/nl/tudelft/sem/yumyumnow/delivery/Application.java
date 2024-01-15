package nl.tudelft.sem.yumyumnow.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EntityScan(basePackages = "nl.tudelft.sem.yumyumnow.delivery.model.*")
@EnableJpaRepositories("nl.tudelft.sem.yumyumnow.delivery.model")
@ComponentScan(basePackages = { "nl.tudelft.sem.yumyumnow.delivery.model" })
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
