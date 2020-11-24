package distributed.tracing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import distributed.tracing.strategies.AverageLatencyCalculator;
import distributed.tracing.strategies.RoutesCounter;
import distributed.tracing.strategies.ShortestPathCalculator;
import distributed.tracing.strategies.TracesCounter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class App {

    public static void main(String... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please, provide full filepath to the text file with tracing example");
        }

        String content = readFile(args[0]);

        //System.out.println("Start with params: " + Arrays.toString(args));
        AverageLatencyCalculator calculator = new AverageLatencyCalculator(content);
        System.out.println(calculator.calculate("ABC"));
        System.out.println(calculator.calculate("AD"));
        System.out.println(calculator.calculate("ADC"));
        System.out.println(calculator.calculate("AEBCD"));
        System.out.println(calculator.calculate("AED"));
        TracesCounter counter = new TracesCounter(calculator.getGraph());

        System.out.println(counter.count('C', 'C', t -> t <= 3));
        System.out.println(counter.count('A', 'C', t -> t == 4));
        ShortestPathCalculator shortestPathCalculator = new ShortestPathCalculator(calculator.getGraph());

        System.out.println(shortestPathCalculator.calculate('A', 'C'));
        System.out.println(shortestPathCalculator.calculate('B', 'B'));

        RoutesCounter routesCounter = new RoutesCounter(calculator.getGraph());
        System.out.println(routesCounter.count('C', 'C', 30));
    }

    private static String readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes, UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file content from path: " + filePath);
        }
    }
}
