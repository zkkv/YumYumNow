package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
public class OrderService {
    private final RestTemplate restTemplate;
    private final String orderServiceUrl;

    /**
     * Creates a new Order Service.
     *
     * @param restTemplate the RestTemplate object used for making HTTP requests to the Order microservice.
     * @param orderServiceUrl the url of the Order Microservice.
     */
    @Autowired
    public OrderService(RestTemplate restTemplate, @Value("${order.microservice.url}") String orderServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
    }

    /**
     * Finds an order by id.
     *
     * @param orderId the order id.
     * @return an Order object or null if the order could not be retrieved.
     */
    public Order findOrderById(UUID orderId) {
        try {
            String url = orderServiceUrl + "/order/" + orderId;
            return restTemplate.getForObject(url, Order.class);
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    /**
     * Checks if an order is paid.
     *
     * @param orderId the id of the order.
     * @return true if the order is paid and false otherwise.
     * @throws HttpClientErrorException if an error happened when retrieving the information.
     */
    public boolean isPaid(UUID orderId) throws HttpClientErrorException {
        String url = orderServiceUrl + "/order/" + orderId + "/isPaid";
        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
        return response.getBody() != null && response.getBody();
    }

    /**
     * Change the paid status of an order.
     * If the orderPaid field is true make it false or true otherwise.
     *
     * @param orderId the id of the order
     */
    public void changePaidStatus(UUID orderId) {
        String url = orderServiceUrl + "/order/" + orderId + "/isPaid";
        restTemplate.put(url, null);
    }

    //TODO: change from String to the Enum Status class?
    /**
     * Gets the status of an order.
     *
     * @param orderId the id of the order.
     * @return a string representing the status.
     * @throws HttpClientErrorException if an error happened when retrieving the information.
     */
    public String getStatus(UUID orderId) throws HttpClientErrorException {
        String url = orderServiceUrl + "/order/" + orderId + "/status";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId the id of the order.
     * @param newStatus the new status of the order.
     */
    public void updateStatus(UUID orderId, String newStatus) {
        String url = orderServiceUrl + "/order/" + orderId + "/status";
        HttpEntity<String> requestEntity = new HttpEntity<>(newStatus);
        restTemplate.put(url, requestEntity);
    }


}
