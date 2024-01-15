package nl.tudelft.sem.yumyumnow.delivery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        // Try executing a simple SQL query to check the database connection
        jdbcTemplate.execute("SELECT 1");

        // If the query executes without errors, the connection is successful
        assertThat(true).isTrue();
    }
}