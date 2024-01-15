package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryAdminMaxZoneGet200Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final AdminValidatorService adminValidatorService;
    private final GlobalConfigRepository globalConfigRepository;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;

    /**
     * Constructor for admin service.
     *
     * @param orderService              the service for order
     * @param deliveryRepository        the repository for delivery
     * @param globalConfigRepository    the repository for global config
     */
    @Autowired
    public AdminService(OrderService orderService,
                        DeliveryRepository deliveryRepository,
                        AdminValidatorService adminValidatorService,
                        GlobalConfigRepository globalConfigRepository) {
        this.orderService = orderService;
        this.deliveryRepository = deliveryRepository;
        this.adminValidatorService = adminValidatorService;
        this.globalConfigRepository = globalConfigRepository;
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
        if (!adminValidatorService.validate(adminId)) {
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
        if (!adminValidatorService.validate(adminId)) {
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
        if (!adminValidatorService.validate(adminId)) {
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
        if (!adminValidatorService.validate(adminId)) {
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
     * @param adminService admin service from user microservice
     * @return the response contains admin id and default maximum delivery zone
     */
    public DeliveryAdminMaxZoneGet200Response adminGetMaxZone(UUID adminId, AdminService adminService)
            throws AccessForbiddenException, ServiceUnavailableException {

        if (!adminValidatorService.validate(adminId)) {
            throw new AccessForbiddenException("User has no right to get default max zone.");
        }

        Optional<GlobalConfig> optionalGlobalConfig = globalConfigRepository.findById(globalConfigId);
        if (optionalGlobalConfig.isEmpty()) {
            return null;
        }
        GlobalConfig globalConfig = optionalGlobalConfig.get();
        BigDecimal defaultMaxZone = globalConfig.getDefaultMaxZone();

        DeliveryAdminMaxZoneGet200Response response = new DeliveryAdminMaxZoneGet200Response();
        response.setAdminId(adminId);
        response.setRadiusKm(defaultMaxZone);
        return response;
    }

    /**
     * Set a new default maximum delivery zone as an admin.
     *
     * @param adminId the id of admin
     * @param defaultMaxZone the new default maximum delivery zone
     * @param adminService admin service from user microservice
     * @return the response contains admin id and updated default maximum delivery zone
     */
    public DeliveryAdminMaxZoneGet200Response adminSetMaxZone(UUID adminId, BigDecimal defaultMaxZone,
                                                              AdminService adminService)
            throws AccessForbiddenException, ServiceUnavailableException {

        if (!adminValidatorService.validate(adminId)) {
            throw new AccessForbiddenException("User has no right to get default max zone.");
        }

        Optional<GlobalConfig> optionalGlobalConfig = globalConfigRepository.findById(globalConfigId);
        if (optionalGlobalConfig.isEmpty()) {
            return null;
        }
        GlobalConfig globalConfig = optionalGlobalConfig.get();
        globalConfig.setDefaultMaxZone(defaultMaxZone);
        globalConfigRepository.save(globalConfig);

        DeliveryAdminMaxZoneGet200Response response = new DeliveryAdminMaxZoneGet200Response();
        response.setAdminId(adminId);
        response.setRadiusKm(defaultMaxZone);

        return response;
    }

}
