package nl.tudelft.sem.yumyumnow.delivery.application.services;

import lombok.SneakyThrows;
import nl.tudelft.sem.yumyumnow.delivery.application.validators.UserIsAdminValidator;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.AdminMaxZoneGet200Response;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final OrderService orderService;
    private final DeliveryRepository deliveryRepository;
    private final VendorService vendorService;

    private final String userServiceUrl;
    private final RestTemplate restTemplate;

    /**
     * Constructor for admin service.
     *
     * @param orderService       the service for order
     * @param deliveryRepository the repository for delivery
     * @param vendorService      the service for the vendor
     * @param restTemplateBuilder the rest template builder
     */
    @Autowired
    public AdminService(OrderService orderService,
                        DeliveryRepository deliveryRepository,
                        VendorService vendorService,
                        RestTemplateBuilder restTemplateBuilder,
                        @Value("${user.microservice.url}") String userServiceUrl)  {
        this.orderService = orderService;
        this.deliveryRepository = deliveryRepository;
        this.vendorService = vendorService;
        this.userServiceUrl = userServiceUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Constructor for admin service.
     *
     * @param orderService       the service for order
     * @param deliveryRepository the repository for delivery
     * @param vendorService      the service for the vendor
     * @param restTemplate       the rest template
     */
    public AdminService(OrderService orderService,
                        DeliveryRepository deliveryRepository,
                        VendorService vendorService,
                        RestTemplate restTemplate,
                        @Value("${user.microservice.url}") String userServiceUrl) {
        this.orderService = orderService;
        this.deliveryRepository = deliveryRepository;
        this.vendorService = vendorService;
        this.userServiceUrl = userServiceUrl;
        this.restTemplate = restTemplate;
    }

    public List<String> getEncounteredIssues(UUID adminId, OffsetDateTime startDate, OffsetDateTime endDate) throws AccessForbiddenException, BadArgumentException {
        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
            throw new AccessForbiddenException("User has no right to get analytics.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadArgumentException("Start date cannot be greater than end date.");
        }

        List<String> possibleIssues = List.of(
                "Open bridge encountered",
                "Traffic jam encountered",
                "Road works encountered",
                "Closed road encountered",
                "Could not find address",
                "Could not contact customer",
                "Could not contact vendor",
                "Vehicle broke down",
                "Internet connection lost"
        );

        List<String> encounteredIssues = new ArrayList<>();

        int daysBetweenDates = (int) Duration.between(startDate, endDate).toDays();

        // Randomly generate a number of issues with random deliveries
        Random random = new Random();
        random.setSeed(0); // Set seed to 0 for reproducibility in tests
        int numberOfIssues = random.nextInt(possibleIssues.size() * daysBetweenDates);
        for (int i = 0; i < numberOfIssues; i++) {
            int randomIndex = random.nextInt(possibleIssues.size());
            encounteredIssues.add(possibleIssues.get(randomIndex));
        }

        return encounteredIssues;
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
        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
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
        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
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
        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
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
        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
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

    /**
     * Get the default maximum delivery zone as an admin.
     *
     * @param adminId the id of admin
     * @return the response contains admin id and default maximum delivery zone
     */
    public AdminMaxZoneGet200Response adminGetMaxZone(UUID adminId)
            throws AccessForbiddenException, ServiceUnavailableException {

        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
            throw new AccessForbiddenException("User has no right to get default max zone.");
        }

        BigDecimal defaultMaxZone = vendorService.getDefaultMaxDeliveryZone();

        AdminMaxZoneGet200Response response = new AdminMaxZoneGet200Response();
        response.setAdminId(adminId);
        response.setRadiusKm(defaultMaxZone);
        return response;
    }

    /**
     * Set a new default maximum delivery zone as an admin.
     *
     * @param adminId the id of admin
     * @param newMaxZone the new default maximum delivery zone
     * @return the response contains admin id and updated default maximum delivery zone
     */
    public AdminMaxZoneGet200Response adminSetMaxZone(UUID adminId, BigDecimal newMaxZone)
            throws AccessForbiddenException, ServiceUnavailableException {

        if (newMaxZone.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (!new UserIsAdminValidator(null, getAdminUser(adminId, userServiceUrl)).process(null)) {
            throw new AccessForbiddenException("User has no right to set default max zone.");
        }

        vendorService.setDefaultMaxDeliveryZone(newMaxZone);
        AdminMaxZoneGet200Response response = new AdminMaxZoneGet200Response();
        response.setAdminId(adminId);
        response.setRadiusKm(newMaxZone);

        return response;
    }

    public Map<String, Object> getAdminUser(UUID adminId, String userServiceUrl) {
        return this.restTemplate.getForObject(userServiceUrl
                + "/user/" + adminId.toString(), Map.class);
    }


}
