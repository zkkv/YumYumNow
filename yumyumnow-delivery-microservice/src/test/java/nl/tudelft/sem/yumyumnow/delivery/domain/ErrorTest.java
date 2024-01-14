package nl.tudelft.sem.yumyumnow.delivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    @Test
    void testEquals() {
        Error error1 = new Error();
        error1.setCode(404);
        error1.setMessage("Not found");

        Error error2 = new Error();
        error2.setCode(404);
        error2.setMessage("Not found");

        assertEquals(error1, error2);
        assertEquals(error1.getCode(), error2.getCode());
        assertEquals(error1.getMessage(), error2.getMessage());
        assertEquals(error1.getMessage(), "Not found");
        assertEquals(error1.getCode(), 404);
    }

    @Test
    void testNotEqualSameCodeDifferentMessages() {
        Error error1 = new Error();
        error1.setCode(404);
        error1.setMessage("Not found");

        Error error2 = new Error();
        error2.setCode(404);
        error2.setMessage("The resource was not found");

        assertNotEquals(error1, error2);
    }

    @Test
    void testNotEqualDifferentCodesSameMessage() {
        Error error1 = new Error();
        error1.setCode(200);
        error1.setMessage("Error");

        Error error2 = new Error();
        error2.setCode(404);
        error2.setMessage("Error");

        assertNotEquals(error1, error2);
    }

    @Test
    void testEqualSameAddress() {
        Error error = new Error();
        error.setCode(200);
        error.setMessage("Error");

        assertEquals(error, error);
    }

    @Test
    void testNotEqualDifferentClass() {
        Error error = new Error();
        error.setCode(200);
        error.setMessage("Error");

        Object o = new Object();

        assertNotEquals(error, o);
    }

    @Test
    void testNotEqualWithNull() {
        Error error = new Error();
        error.setCode(200);
        error.setMessage("Error");

        assertNotEquals(error, null);
    }

    @Test
    void testHashCode() {
        Error error1 = new Error();
        error1.setCode(404);
        error1.setMessage("Not found");

        Error error2 = new Error();
        error2.setCode(404);
        error2.setMessage("Not found");

        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void testToString() {
        Error error = new Error();
        error.setCode(404);
        error.setMessage(null);

        String expected = """
                class Error {
                    code: 404
                    message: null
                }""";
        assertEquals(expected, error.toString());
    }

    @Test
    void testMessage() {
        Error error = new Error();
        error.setCode(0);
        error.setMessage(null);

        Error expected = new Error();
        expected.setCode(0);
        expected.setMessage("Error");

        assertEquals(expected, error.message("Error"));
    }

    @Test
    void testCode() {
        Error error = new Error();
        error.setCode(0);
        error.setMessage(null);

        Error expected = new Error();
        expected.setCode(404);
        expected.setMessage(null);

        assertEquals(expected, error.code(404));
    }
}