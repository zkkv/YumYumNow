package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyum.delivery.model.Delivery;
import nl.tudelft.sem.yumyum.delivery.model.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository The repository to use.
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Create a delivery based on order data.
     *
     * @param order The order to create a delivery for.
     * @return The created delivery.
     */
    public Delivery createDelivery(Order order) {
        Delivery delivery = new Delivery();

        delivery.setOrderId(order.getId());
        delivery.setVendorId(order.getVendor().getId());
        delivery.setCourierId(order.getCustomer().getId());

        delivery.setStatus(Delivery.StatusEnum.PENDING);

        return deliveryRepository.save(delivery);
    }
}
