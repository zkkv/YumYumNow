package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.application.validators.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryAdminMaxZoneGet200Response;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final GlobalConfigRepository globalConfigRepository;
    private final VendorService vendorService;
    private final CourierService courierService;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;

    private final OrderService orderService;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository The repository to use for delivery
     * @param globalConfigRepository The repository for global configuration
     * @param vendorService service of the vendor
     * @param courierService service of the courier
     * @param orderService service of the order
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository,
                           GlobalConfigRepository globalConfigRepository,
                           VendorService vendorService,
                           CourierService courierService,
                           OrderService orderService) {
        this.deliveryRepository = deliveryRepository;
        this.globalConfigRepository = globalConfigRepository;
        this.vendorService = vendorService;
        this.courierService = courierService;
        this.orderService = orderService;
    }

    /**
     * Create a delivery based on order data.
     *
     * @param orderId The order ID to which the delivery corresponds
     *              (UUID).
     * @param vendorId The vendor ID to which the delivery corresponds
     *               (UUID).
     * @return The created delivery.
     */
    public Delivery createDelivery(UUID orderId, UUID vendorId) throws BadArgumentException {
        Delivery delivery = new Delivery();

        if (vendorService.getVendor(vendorId.toString()) == null) {
            throw new BadArgumentException("Vendor does not exist");
        }

        delivery.setId(UUID.randomUUID());
        delivery.setOrderId(orderId);
        delivery.setVendorId(vendorId);
        delivery.setStatus(Delivery.StatusEnum.PENDING);

        deliveryRepository.save(delivery);
        return delivery;
    }

    /**
     * Update the estimatedPrepTime of a delivery.
     *
     * @param deliveryId        the ID of the delivery to be updated
     * @param estimatedPrepTime the new estimated time
     * @return the updated delivery
     */
    public Delivery changePrepTime(UUID deliveryId, UUID vendorId, OffsetDateTime estimatedPrepTime) {


        Optional<Delivery> optionalDelivery = deliveryRepository.findById(deliveryId);


        if (optionalDelivery.isEmpty()) {
            return null;
        }

        Delivery delivery = optionalDelivery.get();

        VendorBelongsToDeliveryValidator vendorValidator = new VendorBelongsToDeliveryValidator(
                null, vendorId, vendorService);

        if (delivery.getStatus() != Delivery.StatusEnum.ACCEPTED || !vendorValidator.process(delivery)) {
            return null;
        }

        delivery.setEstimatedPreparationFinishTime(estimatedPrepTime);

        deliveryRepository.save(delivery);

        return delivery;
    }

    /**
     * Updates status of the delivery with verification of the user rights.
     *
     * @param id     delivery id.
     * @param userId user id, for valid update user has to be either a courier or a vendor,
     *               depending on which status they are trying to set.
     * @param status the new status of the delivery.
     * @return delivery object with the update status, or null if user has no right to
     *         update it or if delivery is not found.
     * @author Horia Radu, Kirill Zhankov
     */
    public Delivery updateStatus(UUID id, UUID userId, DeliveryIdStatusPutRequest.StatusEnum status)
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {

        if (status == DeliveryIdStatusPutRequest.StatusEnum.PENDING) {
            throw new BadArgumentException("Status cannot be PENDING.");
        }

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("No delivery found by id.");
        }

        if (status == DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED && !orderService.isPaid(id)) {
            throw new AccessForbiddenException("The delivery hasn't been paid for yet.");
        }

        Delivery delivery = optionalDelivery.get();

        StatusPermissionValidator statusPermissionValidator = new StatusPermissionValidator(
                 Map.of(
                         Vendor.class, new VendorExistsValidator(
                                 new VendorBelongsToDeliveryValidator(null, userId, vendorService),
                                 userId, vendorService),
                         Courier.class, new CourierExistsValidator(
                                 new CourierBelongsToVendorValidator(
                                 new CourierBelongsToDeliveryValidator(null, userId, courierService),
                                 userId, courierService, vendorService), userId, courierService)
                 ), status, userId, vendorService, courierService);



        if (!statusPermissionValidator.process(delivery)) {
            throw new AccessForbiddenException("User has no right to update delivery status.");
        }

        switch (status) {
            case ACCEPTED -> delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
            case REJECTED -> delivery.setStatus(Delivery.StatusEnum.REJECTED);
            case DELIVERED -> delivery.setStatus(Delivery.StatusEnum.DELIVERED);
            case PREPARING -> delivery.setStatus(Delivery.StatusEnum.PREPARING);
            case IN_TRANSIT -> delivery.setStatus(Delivery.StatusEnum.IN_TRANSIT);
            case GIVEN_TO_COURIER -> delivery.setStatus(Delivery.StatusEnum.GIVEN_TO_COURIER);
            default -> throw new BadArgumentException(
                    "Status can only be one of: ACCEPTED, REJECTED, DELIVERED, "
                            + "PREPARING, IN_TRANSIT, GIVEN_TO_COURIER");
        }

        deliveryRepository.save(delivery);

        return delivery;
    }


    /**
     * Update the maximum delivery zone of a vendor.
     *
     * @param vendorId                          the current vendorId
     * @param deliveryVendorIdMaxZonePutRequest contains id for the vendor to update (should be the same as current vendorId)
     *                                          and the new maximium delivery zone
     * @param vendorService                     vendor service to interact with user api
     * @return the vendorID with its updated maximum delivery zone
     */
    public DeliveryVendorIdMaxZonePutRequest vendorMaxZone(
            UUID vendorId,
            DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest,
            VendorService vendorService) {

        UUID vendorToUpdate = deliveryVendorIdMaxZonePutRequest.getVendorId();
        BigDecimal radiusKm = deliveryVendorIdMaxZonePutRequest.getRadiusKm();

        if (vendorId != vendorToUpdate || vendorService.getVendor(vendorId.toString()) == null) {
            return null;
        }

        Vendor vendor = vendorService.getVendor(vendorId.toString());

        if (vendor.getAllowsOnlyOwnCouriers()) {
            vendor.setMaxDeliveryZoneKm(radiusKm);

            boolean response = vendorService.putVendor(vendor);
            if (response) {
                return deliveryVendorIdMaxZonePutRequest;
            }
        }
        return null;
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

        if (!adminService.validate(adminId)) {
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

        if (!adminService.validate(adminId)) {
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

    /**
     * Returns the delivery specified by {@code id} from deliveryRepository, or throws
     * {@link NoDeliveryFoundException} if it's not present.
     *
     * @param id UUID of the delivery.
     * @return the delivery object if it's present in the repository or null otherwise.
     * @throws NoDeliveryFoundException if delivery was not found.
     * @author Kirill Zhankov
     */
    public Delivery getDelivery(UUID id) throws NoDeliveryFoundException {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("No delivery found by id.");
        }
        return optionalDelivery.get();
    }

    private static final int EARTH_RADIUS_KM = 6378; // constant for the earth radius needed to calculate the distance
    private static final int AVERAGE_SPEED_KMH = 50; // constant for the average speed

    /**
     * This method calculates the delivery time based on the distance between the vendor and the customer.
     *
     * @param customerLocation the customer's location stored as a Location object.
     * @param vendorLocation   the vendor's location stored as a Location object.
     * @return the time expressed in seconds.
     */
    public Duration getDeliveryTimeHelper(Location customerLocation, @Valid DeliveryCurrentLocation vendorLocation) {
        // Convert the latitudes and longitudes from degrees to radians.
        double lat1 = customerLocation.getLatitude().doubleValue();
        double lat2 = vendorLocation.getLatitude().doubleValue();
        double long1 = customerLocation.getLongitude().doubleValue();
        double long2 = vendorLocation.getLongitude().doubleValue();
        double latDistance = Math.toRadians(lat1 - lat2);
        double lonDistance = Math.toRadians(long1 - long2);

        // Apply the Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = EARTH_RADIUS_KM * c;

        double timeInHours = dist / AVERAGE_SPEED_KMH; // the average speed is set to 50 km/h
        long timeInSeconds = (long) (timeInHours * 3600); // Convert time to seconds
        return Duration.ofSeconds(timeInSeconds);
    }

    /**
     * This method adds the total delivery time to a delivery.
     *
     * @param deliveryId   the id of the delivery.
     * @param orderService the instance of the order service.
     * @param userService  the instance of the user service.
     * @return a Delivery object representing the update delivery.
     * @throws Exception the exception to be thrown.
     *
     */
    public Delivery addDeliveryTime(UUID deliveryId, OrderService orderService, CustomerService userService)
            throws Exception {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(deliveryId);
        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("You cannot update the time of a non-existing delivery.");
        }

        Delivery delivery = optionalDelivery.get();

        // get preparation time
        final OffsetDateTime preparationTime = delivery.getEstimatedPreparationFinishTime();

        // get the location of the customer
        UUID orderId = delivery.getOrderId();
        Order order = orderService.findOrderById(orderId);
        if (order == null) {
            throw new BadArgumentException("The order is non-existent.");
        }
        Customer customer = order.getCustomer();
        if (customer == null) {
            throw new BadArgumentException("The customer is non-existing.");
        }
        Location customerLocation = userService.getCustomerAddress(customer.getId());
        if (customerLocation == null) {
            throw new BadArgumentException("The customer's location is non-existing.");
        }

        // location of vendor
        @Valid DeliveryCurrentLocation vendorLocation = delivery.getCurrentLocation();
        if (vendorLocation == null) {
            throw new BadArgumentException("The vendor's location is non-existing.");
        }
        Duration deliveryTime = getDeliveryTimeHelper(customerLocation, vendorLocation);

        OffsetDateTime totalTime = preparationTime.plus(deliveryTime);
        delivery.setEstimatedDeliveryTime(totalTime);

        deliveryRepository.save(delivery);
        return delivery;
    }

    /**
     * Assigns courier with the provided {@code courierId} to the delivery
     * with the given {@code id}.
     *
     * @param id id of the delivery
     * @param courierId id of the courier
     * @return delivery after assigning the courier to it
     * @author Kirill Zhankov
     */
    public Delivery assignCourier(UUID id, UUID courierId)
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("No delivery found by id.");
        }

        Delivery delivery = optionalDelivery.get();
        UUID vendorId = delivery.getVendorId();

        if (vendorId == null) {
            throw new BadArgumentException("Delivery has no vendor assigned.");
        }

        var validator = new CourierExistsValidator(
                        new VendorExistsValidator(
                        new CourierBelongsToVendorValidator(null,
                        courierId, courierService, vendorService),
                        delivery.getVendorId(), vendorService),
                courierId, courierService);

        if (!validator.process(delivery)) {
            throw new AccessForbiddenException("Courier does not belong to the vendor");
        }

        UUID oldCourierId = delivery.getCourierId();
        if (courierId.equals(oldCourierId)) {
            throw new BadArgumentException("Courier with that id is assigned to the delivery.");
        }

        if (oldCourierId != null && !courierId.equals(oldCourierId)) {
            throw new AccessForbiddenException("Another courier is assigned to the delivery");
        }


        delivery.setCourierId(courierId);
        deliveryRepository.save(delivery);

        return delivery;
    }
}
