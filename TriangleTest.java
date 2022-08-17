package unittest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TriangleTest {

    @Test
    public void testCase1() { assertEquals(Triangle.Isosceles, checkTriangle(12, 12, 19)); }

    @Test
    public void testCase2() { assertEquals(Triangle.Scalene, checkTriangle(17, 15, 16)); }


}


