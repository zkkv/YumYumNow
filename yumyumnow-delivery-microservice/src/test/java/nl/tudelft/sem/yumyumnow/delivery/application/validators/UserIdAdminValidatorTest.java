package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class UserIdAdminValidatorTest {
    AdminService adminService;

    @BeforeEach
    void setUp(){
        adminService = mock(AdminService.class);
    }
    @Test
    void userIsAdminProcessTest() {
        Map<String, Object> adminUser = new HashMap<>();
        adminUser.put("userType", "Admin");
        UserIsAdminValidator userIsAdminValidator = new UserIsAdminValidator(null, adminUser);
        assertTrue(userIsAdminValidator.process(null));
    }

    @Test
    void userIsNotAdminProcessTest() {
        Map<String, Object> adminUser = new HashMap<>();
        adminUser.put("userType", "Customer");
        UserIsAdminValidator userIsAdminValidator = new UserIsAdminValidator(null, adminUser);
        assertFalse(userIsAdminValidator.process(null));
    }

    @Test
    void processChainTest() {
        Map<String, Object> adminUser = new HashMap<>();
        adminUser.put("userType", "Admin");
        UserIsAdminValidator userIsAdminValidator = new UserIsAdminValidator(null, adminUser);
        UserIsAdminValidator userIsAdminValidator2 = new UserIsAdminValidator(userIsAdminValidator, adminUser);
        assertTrue(userIsAdminValidator2.process(null));
    }
}
