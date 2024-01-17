package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.application.validators.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
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
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final VendorService vendorService;
    private final CourierService courierService;
    private final OrderService orderService;
    private final EmailService emailService;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository The repository to use for delivery
     * @param vendorService      service of the vendor
     * @param courierService     service of the courier
     * @param orderService       service of the order
     * @param emailService       service of emails
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository,
                           VendorService vendorService,
                           CourierService courierService,
                           OrderService orderService,
                           EmailService emailService) {
        this.deliveryRepository = deliveryRepository;
        this.vendorService = vendorService;
        this.courierService = courierService;
        this.orderService = orderService;
        this.emailService = emailService;

        // Initialize a default delivery for TA purposes
        Delivery d = new DeliveryBuilder()
                .setId(UUID.fromString("131304a3-693b-4550-b4e7-2438a2d686d0"))
                .setOrderId(UUID.fromString("c93e6205-c256-484f-8ebf-e76d625d3444"))
                .setVendorId(UUID.fromString("aec02858-71ae-40d6-a252-7d8b45338d91"))
                .setStatus(Delivery.StatusEnum.PENDING)
                .create();
        deliveryRepository.save(d);
        System.out.println("Created a default delivery with id: " + d.getId());
    }

    /**
     * Create a delivery based on order data.
     *
     * @param orderId  The order ID to which the delivery corresponds
     *                 (UUID).
     * @param vendorId The vendor ID to which the delivery corresponds
     *                 (UUID).
     * @return The created delivery.
     */
    public Delivery createDelivery(UUID orderId, UUID vendorId) throws BadArgumentException {

        if (vendorService.getVendor(vendorId.toString()) == null) {
            throw new BadArgumentException("Vendor does not exist");
        }
        if (orderService.findOrderById(orderId) == null) {
            throw new BadArgumentException("Order does not exist");
        }
        Delivery delivery = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setOrderId(orderId)
                .setVendorId(vendorId)
                .setStatus(Delivery.StatusEnum.PENDING)
                .create();

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

        if (status == DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED && !orderService.isPaid(delivery.getOrderId())) {
            throw new AccessForbiddenException("The delivery hasn't been paid for yet.");
        }


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
            case IN_TRANSIT -> {
                delivery.setStatus(Delivery.StatusEnum.IN_TRANSIT);

            }
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
     *                                          and the new maximum delivery zone
     * @param vendorService                     vendor service to interact with user api
     * @return the vendorID with its updated maximum delivery zone
     */
    public DeliveryVendorIdMaxZonePutRequest vendorMaxZone(
            UUID vendorId,
            DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest,
            VendorService vendorService) {

        UUID vendorToUpdate = deliveryVendorIdMaxZonePutRequest.getVendorId();
        BigDecimal radiusKm = deliveryVendorIdMaxZonePutRequest.getRadiusKm();

        if (!vendorId.equals(vendorToUpdate)  || vendorService.getVendor(vendorId.toString()) == null) {

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


    /**
     * Helper method to calculate the distance between two locations.
     *
     * @param location1 first given location
     * @param location2 second given location
     * @return the distance as double
     */
    public Double distanceBetween(Location location1, DeliveryCurrentLocation location2) {
        // Convert the latitudes and longitudes from degrees to radians.
        double lat1 = location1.getLatitude().doubleValue();
        double lat2 = location2.getLatitude().doubleValue();
        double long1 = location1.getLongitude().doubleValue();
        double long2 = location2.getLongitude().doubleValue();
        double latDistance = Math.toRadians(lat1 - lat2);
        double lonDistance = Math.toRadians(long1 - long2);

        // Apply the Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = EARTH_RADIUS_KM * c;

        return dist;
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
        double dist = distanceBetween(customerLocation, vendorLocation);

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
     */
    public Delivery addDeliveryTime(UUID deliveryId, OrderService orderService, CustomerService userService)
            throws NoDeliveryFoundException, BadArgumentException {
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
     * @param id        id of the delivery
     * @param courierId id of the courier
     * @return delivery after assigning the courier to it
     * @author Kirill Zhankov
     */
    public Delivery assignCourier(UUID id, UUID courierId)
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        // Check if delivery is present.
        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("No delivery found by id.");
        }

        // Check if delivery has a vendor associated with it.
        Delivery delivery = optionalDelivery.get();
        UUID vendorId = delivery.getVendorId();

        if (vendorId == null) {
            throw new BadArgumentException("Delivery has no vendor assigned.");
        }

        // Chain of validators that checks that courier is associated with vendor.
        var validator = new CourierExistsValidator(
                new VendorExistsValidator(
                        new CourierBelongsToVendorValidator(null,
                                courierId, courierService, vendorService),
                        delivery.getVendorId(), vendorService),
                courierId, courierService);

        if (!validator.process(delivery)) {
            throw new AccessForbiddenException("Courier does not belong to the vendor");
        }

        // Check if the delivery already has a courier with the same id.
        UUID oldCourierId = delivery.getCourierId();
        if (courierId.equals(oldCourierId)) {
            throw new BadArgumentException("Courier with that id is assigned to the delivery.");
        }

        // Check if the delivery already has a courier with a different id.
        if (oldCourierId != null && !courierId.equals(oldCourierId)) {
            throw new AccessForbiddenException("Another courier is assigned to the delivery");
        }

        delivery.setCourierId(courierId);
        deliveryRepository.save(delivery);

        return delivery;
    }

    /**
     * Creates the email for notifying a customer that the status for their order has been updated.
     *
     * @param status the updated status
     * @param id     id of the delivery
     * @return a confirmation string that the email has been sent
     * @throws BadArgumentException if the order, customer or email cannot be found
     */
    public String sendEmail(DeliveryIdStatusPutRequest.StatusEnum status, UUID id) throws BadArgumentException {
        Delivery delivery = deliveryRepository.findById(id).get();

        Order order = orderService.findOrderById(delivery.getOrderId());

        if (order == null) {
            throw new BadArgumentException("Delivery isn't linked to an order");
        }

        Customer customer = order.getCustomer();

        if (customer == null) {
            throw new BadArgumentException("Order doesn't have a customer");
        }

        String email = customer.getEmail();

        if (email == null) {
            throw new BadArgumentException("Customer doesn't have an email");
        }

        String emailText = "The status of your order has been changed to " + status.getValue();

        return emailService.send(emailText, email);
    }

    /**
     * Returns a list of all deliveries available for a courier ordered by distance.
     *
     * @param radius    maximum distance from the courier
     * @param location  the current location of the courier
     * @param courierId the id of the courier
     * @return the list aforementioned
     * @throws AccessForbiddenException    if the user trying to access is not a courier
     * @throws BadArgumentException        if the radius is invalid
     * @throws ServiceUnavailableException if there is a failure at other services
     */
    public List<Delivery> getAvailableDeliveries(BigDecimal radius, Location location, UUID courierId)
            throws AccessForbiddenException, BadArgumentException, ServiceUnavailableException {
        Courier courier = courierService.getCourier(courierId.toString());
        if (courier == null) {
            throw new AccessForbiddenException("User is not a courier.");
        }

        if (radius.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadArgumentException("Invalid radius value");
        }

        if (location.getLatitude() == null || location.getLongitude() == null) {
            location.setLatitude(BigDecimal.ZERO);
            location.setLongitude(BigDecimal.ZERO);
            //set a default location if none is provided
            //this is such that courier can have a general idea of what orders exist
            //without necessarily having their location turned on
        }

        List<Delivery> deliveries = deliveryRepository.findAll();

        List<Delivery> checkedDeliveries = deliveries
                .stream()
                .filter(d -> d.getCourierId() == null) //Only unassigned orders
                .filter(d -> new CourierBelongsToVendorValidator(
                            null, courierId, courierService, vendorService
                    ).process(d)) //Check if the courier is eligible to see this order
                .filter(d -> radius.compareTo(BigDecimal.valueOf(distanceBetween(location, d.getCurrentLocation()))) >= 0)
                .collect(Collectors.toList());

        checkedDeliveries.sort((x, y) -> {
            Double distX = distanceBetween(location, x.getCurrentLocation());
            Double distY = distanceBetween(location, y.getCurrentLocation());

            return distX.compareTo(distY);
        });

        return checkedDeliveries;
    }

    /**
     * Updates the current location of a delivery.
     *
     * @param id the delivery id
     * @param location the new delivery location
     * @return the delivery with the location changed
     * @throws NoDeliveryFoundException
     */
    public Delivery updateLocation(UUID id, Location location) throws NoDeliveryFoundException {
        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("No delivery found by id.");
        }

        Delivery delivery = optionalDelivery.get();
        DeliveryCurrentLocation currentLocation = new DeliveryCurrentLocation()
                .timestamp(location.getTimestamp())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude());
        delivery.setCurrentLocation(currentLocation);
        deliveryRepository.save(delivery);
        return delivery;
    }
}

