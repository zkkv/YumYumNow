package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final OrderService orderService;
    private final DeliveryRepository deliveryRepository;

    /**
     * Constructor for admin service.
     *
     * @param restTemplate       restTemplate to interact with other api
     * @param userServiceUrl     url for user microservice
     * @param orderService
     * @param deliveryRepository
     */
    @Autowired
    public AdminService(RestTemplate restTemplate,
                        @Value("${user.microservice.url}") String userServiceUrl,
                        OrderService orderService, DeliveryRepository deliveryRepository) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.orderService = orderService;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Validate the admin.
     *
     * @param adminId The admin to validate
     * @return The validation result.
     * @throws ServiceUnavailableException Exception if the service of other microservice is unavailable.
     */
    public boolean validate(UUID adminId) throws ServiceUnavailableException {
        try {
            Map<String, Object> responseUser = restTemplate.getForObject(userServiceUrl
                    + "/" + adminId.toString(), Map.class);
            String type = (String) responseUser.get("userType");
            if (Objects.equals(type, "Admin")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
    }

    /**
     * Count the total number of deliveries between two given dates.
     *
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return an Integer representing the total number of deliveries
     * @throws BadArgumentException when the provided arguments are wrong
     */

    public int getTotalDeliveriesAnalytic(UUID adminId, OffsetDateTime startDate, OffsetDateTime endDate)
            throws BadArgumentException, AccessForbiddenException, ServiceUnavailableException {
        if (!validate(adminId)) {
            throw new AccessForbiddenException("User has no right to get analytics.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadArgumentException("Start date cannot be greater than end date.");
        }

        List<Delivery> deliveries = deliveryRepository.findAll();
        List<Delivery> filteredDeliveries = deliveries.stream()
                .filter(x -> x.getEstimatedDeliveryTime().isAfter(startDate)
                        && x.getEstimatedDeliveryTime().isBefore(endDate))
                .collect(Collectors.toList());
        return filteredDeliveries.size();
    }

    /**
     * Count the total number of successful deliveries between two given dates.
     *
     * @param adminId the id of the admin
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return an Integer representing the total number of successful deliveries
     * @throws BadArgumentException when the provided arguments are wrong
     */
    public int getSuccessfulDeliveriesAnalytic(UUID adminId, OffsetDateTime startDate, OffsetDateTime endDate)
            throws BadArgumentException, AccessForbiddenException, ServiceUnavailableException {
        if (!validate(adminId)) {
            throw new AccessForbiddenException("User has no right to get analytics.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadArgumentException("Start date cannot be greater than end date.");
        }

        List<Delivery> deliveries = deliveryRepository.findAll();
        List<Delivery> filteredDeliveries = deliveries.stream()
                .filter(x -> x.getStatus() == Delivery.StatusEnum.DELIVERED
                        && x.getEstimatedDeliveryTime().isAfter(startDate)
                        && x.getEstimatedDeliveryTime().isBefore(endDate))
                .collect(Collectors.toList());
        return filteredDeliveries.size();
    }

    /**
     * Get the average time of order preparation between two given dates.
     *
     * @param adminId the id of the admin
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return an Integer representing the number of minutes spent on average on preparing an order
     * @throws AccessForbiddenException when the user has no right to access the analytics
     * @throws BadArgumentException when the provided arguments are wrong
     * @throws ServiceUnavailableException when the service does not respond
     */
    public long getPreparationTimeAnalytic(UUID adminId, OffsetDateTime startDate, OffsetDateTime endDate)
            throws AccessForbiddenException, BadArgumentException, ServiceUnavailableException {
        if (!validate(adminId)) {
            throw new AccessForbiddenException("User has no right to get analytics.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadArgumentException("Start date cannot be greater than end date.");
        }

        List<Delivery> deliveries = deliveryRepository.findAll();
        long totalSum = 0;
        long numberOfDeliveries = 0;

        List<Delivery> filteredDeliveries = deliveries.stream()
                .filter(x -> x.getStatus() == Delivery.StatusEnum.DELIVERED
                        && x.getEstimatedDeliveryTime().isAfter(startDate)
                        && x.getEstimatedDeliveryTime().isBefore(endDate))
                .collect(Collectors.toList());

        for (Delivery delivery : filteredDeliveries) {
            BigDecimal timePlacement = orderService.getTimeOfPlacement(delivery.getOrderId());
            if (timePlacement == null) {
                continue;
            }
            OffsetDateTime preparationFinishTime =  delivery.getEstimatedPreparationFinishTime();
            // Convert both times to Instant.
            Instant startInstant = Instant.ofEpochMilli(timePlacement.longValue());
            Instant endInstant = preparationFinishTime.toInstant();

            totalSum += Duration.between(startInstant, endInstant).toMinutes();
            numberOfDeliveries++;
        }

        return totalSum / numberOfDeliveries;

    }

    /**
     * Get the average duration of a delivery between two given dates.
     *
     * @param adminId the id of the admin
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return an Integer representing the number of minutes spent on average delivering an order
     * @throws AccessForbiddenException when the user has no right to access the analytics
     * @throws BadArgumentException when the provided arguments are wrong
     * @throws ServiceUnavailableException when the service does not respond
     */
    public long getDeliveryTimeAnalytic(UUID adminId, OffsetDateTime startDate, OffsetDateTime endDate)
            throws AccessForbiddenException, BadArgumentException, ServiceUnavailableException {
        if (!validate(adminId)) {
            throw new AccessForbiddenException("User has no right to get analytics.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadArgumentException("Start date cannot be greater than end date.");
        }

        List<Delivery> relevantDeliveries = deliveryRepository.findAll()
                .stream()
                .filter(x -> x.getStatus() == Delivery.StatusEnum.DELIVERED
                        && x.getEstimatedDeliveryTime().isAfter(startDate)
                        && x.getEstimatedDeliveryTime().isBefore(endDate))
                .collect(Collectors.toList());
        long totalTime = 0;
        long numberOfDeliveries = 0;
        for (Delivery delivery : relevantDeliveries) {
            OffsetDateTime preparationFinishTime =  delivery.getEstimatedPreparationFinishTime();
            OffsetDateTime deliveryTime = delivery.getEstimatedDeliveryTime();
            Duration difference = Duration.between(preparationFinishTime, deliveryTime);

            totalTime += difference.toMinutes();
            numberOfDeliveries++;
        }
        return totalTime / numberOfDeliveries;
    }
}
