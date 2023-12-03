package nl.tudelft.sem.yumyumnow.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Example microservice application.
 */
@SpringBootApplication
//@EnableJpaRepositories("nl.tudelft.sem.yumyumnow.delivery.domain.repos")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
