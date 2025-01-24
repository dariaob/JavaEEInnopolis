import model.Circle;
import model.Ellipse;
import model.Figure;
import model.Square;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        try(BufferedReader reader =
                    new BufferedReader(new FileReader("./app/input.txt"))) {

            Map<String, Figure> shapesMap = reader.lines().map(
                    line -> {
                        String[] wordParts = line.split(":");
                        System.out.println(Arrays.toString(wordParts));
                        String id = wordParts[0].trim();
                        String[] parameters = wordParts[1].split(",");

                        Map<String, Integer> paramsMap =  Arrays.stream(parameters)
                                .map(param -> param.split("="))
                                .collect(Collectors.toMap(
                                        param -> param[0].trim(),
                                        param -> Integer.parseInt(param[1].trim())
                                ));
                        int a = paramsMap.get("a");
                        int b = paramsMap.get("b");

                        // Создаем фигуру на основе id
                        if (id.startsWith("square")) {
                            return new AbstractMap.SimpleEntry<>(id, new Square(a, b));
                        } else if (id.startsWith("circle")) {
                            return new AbstractMap.SimpleEntry<>(id, new Circle(a, b));
                        } else {
                            return null;
                        }
                    }).filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // фильтруем, чтобы передать значения движения у квадрата
            shapesMap.values().stream()
                            .filter(value -> value instanceof Square)
                            .forEach(square -> ((Square) square).move(4, 5));

            // фильтруем, чтобы передать значения у движения у круга
            shapesMap.values().stream()
                    .filter(value -> value instanceof Circle)
                    .forEach(circle -> ((Circle) circle).move(3, 2));

            for (Map.Entry<String, Figure> entry : shapesMap.entrySet()) {
                System.out.println(entry.getValue());
            }

            writeDataToFile(shapesMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Запись в файл
     * @param map
     */
    public static void writeDataToFile(Map<String, Figure> map) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write("\n");
            for (Map.Entry<String, Figure> entry : map.entrySet()) {
                fileWriter.write( entry.getValue() + System.lineSeparator());
            }
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}