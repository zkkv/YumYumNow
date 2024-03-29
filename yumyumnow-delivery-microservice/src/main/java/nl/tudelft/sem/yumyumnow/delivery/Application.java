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
@EnableJpaRepositories("nl.tudelft.sem.yumyumnow.delivery.*")
@ComponentScan(basePackages = {"nl.tudelft.sem.yumyumnow.delivery.*" })
@EntityScan(basePackages = "nl.tudelft.sem.yumyumnow.delivery.*")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
