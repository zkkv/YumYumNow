package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.application.validators.CourierBelongsToDeliveryValidator;
import nl.tudelft.sem.yumyumnow.delivery.application.validators.CourierBelongsToVendorValidator;
import nl.tudelft.sem.yumyumnow.delivery.application.validators.StatusPermissionValidator;
import nl.tudelft.sem.yumyumnow.delivery.application.validators.VendorValidator;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.*;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final VendorService vendorService;
    private final CourierService courierService;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository The repository to use.
     */
    @Autowired
    public DeliveryService(
            DeliveryRepository deliveryRepository,
            VendorService vendorService,
            CourierService courierService
    ) {
        this.deliveryRepository = deliveryRepository;
        this.vendorService = vendorService;
        this.courierService = courierService;
    }

    /**
     * Create a delivery based on order data.
     *
     * @param order  The order ID to which the delivery corresponds
     *               (UUID).
     * @param vendor The vendor ID to which the delivery corresponds
     *               (UUID).
     * @return The created delivery.
     */
    public Delivery createDelivery(UUID order, UUID vendor) {
        Delivery delivery = new Delivery();

        delivery.setOrderId(order);
        delivery.setOrderId(order);
        delivery.setStatus(Delivery.StatusEnum.PENDING);

        return deliveryRepository.save(delivery);
    }

    /**
     * Update the estimatedPrepTime of a delivery
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

        VendorValidator vendorValidator = new VendorValidator(null, vendorId, vendorService);

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
     * update it or if delivery is not found.
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

        Delivery delivery = optionalDelivery.get();

         StatusPermissionValidator statusPermissionValidator = new StatusPermissionValidator(
                 Map.of(
                         Vendor.class, new VendorValidator(null, userId, vendorService),
                         Courier.class, new CourierBelongsToVendorValidator(
                                 new CourierBelongsToDeliveryValidator(null, userId, courierService),
                                 userId, courierService, vendorService)
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
                    "Status can only be one of: ACCEPTED, REJECTED, DELIVERED, " +
                            "PREPARING, IN_TRANSIT, GIVEN_TO_COURIER");
        }

        deliveryRepository.save(delivery);

        return delivery;
    }


    /**
     * Update the maximum delivery zone of a vendor
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
     */
    public Delivery addDeliveryTime(UUID deliveryId, OrderService orderService, CustomerService userService) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(deliveryId);
        if (optionalDelivery.isEmpty()) {
            return null;
        }
        Delivery delivery = optionalDelivery.get();

        // get preparation time
        final OffsetDateTime preparationTime = delivery.getEstimatedPreparationFinishTime();

        // get the location of the customer
        UUID orderId = delivery.getOrderId();
        Order order = orderService.findOrderById(orderId);
        if (order == null) {
            return null;
        }
        Customer customer = order.getCustomer();
        if (customer == null) {
            return null;
        }
        Location customerLocation = userService.getCustomerAddress(customer.getId());
        if (customerLocation == null) {
            return null;
        }

        // location of vendor
        @Valid DeliveryCurrentLocation vendorLocation = delivery.getCurrentLocation();
        if (vendorLocation == null) {
            return null;
        }
        Duration deliveryTime = getDeliveryTimeHelper(customerLocation, vendorLocation);

        OffsetDateTime totalTime = preparationTime.plus(deliveryTime);
        delivery.setEstimatedDeliveryTime(totalTime);

        deliveryRepository.save(delivery);
        return delivery;
    }


}
