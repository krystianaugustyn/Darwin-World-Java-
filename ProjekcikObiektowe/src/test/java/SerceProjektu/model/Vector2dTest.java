package SerceProjektu.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class Vector2dTest {

    @Test
    void testConstructor() {
        Vector2d v1 = new Vector2d(1, 2);
        assertEquals(v1.getX(), 1);
    }
}