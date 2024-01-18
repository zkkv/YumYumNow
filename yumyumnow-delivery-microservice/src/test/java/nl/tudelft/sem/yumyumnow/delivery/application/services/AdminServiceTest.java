package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.application.validators.UserIsAdminValidator;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.AdminMaxZoneGet200Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class AdminServiceTest {

    private DeliveryRepository deliveryRepository;
    private AdminService adminService;
    private OrderService orderService;
    private VendorService vendorService;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.orderService = mock(OrderService.class);
        this.vendorService = mock(VendorService.class);
        this.restTemplate = mock(RestTemplate.class);
        this.adminService = new AdminService(
                orderService, deliveryRepository,
                vendorService, restTemplate,
                "https://testsite.com");
    }

    @Test
    public void adminGetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        when(vendorService.getDefaultMaxDeliveryZone()).thenReturn(BigDecimal.valueOf(20));

        AdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new  AdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        AdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneExceptionTest() throws ServiceUnavailableException {
        UUID adminId = UUID.randomUUID();

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));
        when(vendorService.getDefaultMaxDeliveryZone()).thenReturn(BigDecimal.valueOf(20));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.adminGetMaxZone(adminId);
        });
    }

    @Test
    public void adminSetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal newMaxZone = BigDecimal.valueOf(20);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        AdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new  AdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(newMaxZone);

        AdminMaxZoneGet200Response response = adminService.adminSetMaxZone(adminId, newMaxZone);

        verify(vendorService).setDefaultMaxDeliveryZone(newMaxZone);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminSetMaxZoneExceptionTest() throws ServiceUnavailableException{
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.adminSetMaxZone(adminId, defaultMaxZone);
        });
    }

    @Test
    public void adminSetMaxZoneNotValidTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(-2);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        AdminMaxZoneGet200Response response = adminService.adminSetMaxZone(adminId, defaultMaxZone);

        assertEquals(null, response);
    }

    @Test
    public void getEncounteredIssuesSuccessTest() throws AccessForbiddenException, BadArgumentException {
        UUID adminId = UUID.randomUUID();
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        List<String> expected = generateIssues(startDate, endDate);

        assertThat(adminService.getEncounteredIssues(adminId, startDate, endDate)).isEqualTo(expected);
    }

    @Test
    public void getEncounteredIssuesAccessForbiddenTest() throws AccessForbiddenException, BadArgumentException {
        UUID adminId = UUID.randomUUID();
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.getEncounteredIssues(adminId, startDate, endDate);
        });
    }

    @Test
    public void getEncounteredIssuesBadArgumentTest() throws AccessForbiddenException, BadArgumentException {
        UUID adminId = UUID.randomUUID();
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        assertThrows(BadArgumentException.class, () -> {
            adminService.getEncounteredIssues(adminId, endDate, startDate);
        });
    }

    private static List<String> generateIssues(OffsetDateTime startDate, OffsetDateTime endDate) {
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
}
