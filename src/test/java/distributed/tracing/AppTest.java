package distributed.tracing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import distributed.tracing.strategies.AverageLatencyCalculator;
import distributed.tracing.strategies.RoutesCounter;
import distributed.tracing.strategies.ShortestTraceCalculator;
import distributed.tracing.strategies.TracesCounter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AppTest {
    private static final String TEST_CASE = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";

    @ParameterizedTest
    @CsvSource({"ABC,9", "AD,5", "ADC,13", "AEBCD,22", "AED,NO SUCH TRACE"})
    void testAverageLatency(String route, String latency) {
        AverageLatencyCalculator calculator = new AverageLatencyCalculator(TEST_CASE);
        assertEquals(latency, calculator.calculate(route));
    }

    @ParameterizedTest
    @CsvSource({"AC,9", "AF,5", "AC,13", "ABCD,22", "AD,NO SUCH TRACE"})
    void testFailAverageLatency(String route, String latency) {
        AverageLatencyCalculator calculator = new AverageLatencyCalculator(TEST_CASE);
        assertNotEquals(calculator.calculate(route), latency);
    }

    @Test
    void testTripsCount() {
        TracesCounter counter = new TracesCounter(TEST_CASE);
        assertEquals(counter.count('C', 'C', t -> t <= 3), 2);
        assertEquals(counter.count('A', 'C', t -> t == 4), 3);
    }

    @Test
    void testFailTripsCount() {
        TracesCounter counter = new TracesCounter(TEST_CASE);
        assertNotEquals(counter.count('A', 'C', t -> t == 10), 5);
    }

    @Test
    void testShortestPath() {
        ShortestTraceCalculator calculator = new ShortestTraceCalculator(TEST_CASE);
        assertEquals(9, calculator.calculate('A', 'C'));
        assertEquals(9, calculator.calculate('B', 'B'));
    }

    @Test
    void testFailShortestPath() {
        ShortestTraceCalculator calculator = new ShortestTraceCalculator(TEST_CASE);
        assertNotEquals(12, calculator.calculate('A', 'F'));
    }

    @Test
    void testRoutesCount() {
        RoutesCounter counter = new RoutesCounter(TEST_CASE);
        assertEquals(counter.count('C', 'C', 30), 7);
    }

    @Test
    void testFailRoute() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph(null), "Traces is not provided");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph(""), "Traces is not provided");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("BB5, BB7"), "Trace is duplicated");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("BB5.7, CC4"), "Weight has to be numeric");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("BB-5, CC4"), "Weight has to be non negative");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("BC577B, CB4"), "Weight has to be numeric");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("A5, BC4"), "Trace has to be at least 3 symbols");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("5B5, 5C4"), "Microservice name has to be letter");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Graph("B55, C54"), "Microservice name has to be letter");
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> new RoutesCounter("BE5, CF4").count('D', 'X', 30), "Microservice has not been registered");
    }

}
