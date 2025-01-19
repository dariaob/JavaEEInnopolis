import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader("./app/input.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Пропускаем пустые строки
                if (line.trim().isEmpty()) continue;

                // Разделяем строку на ключ и значение по двоеточию
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim();  // Ключ (например, name)
                    String value = parts[1].trim(); // Значение (например, John Doe)
                    map.put(key, value);  // Добавляем в HashMap
                }
            }

            map.put("string1", "office");
            map.put("animal", "cat");
            map.put("string2", "string2");
            map.put("string3", "string3");
            map.put("string4", "string4");

            map.put("animal", "dog");

            List<String> array = new ArrayList<>(map.keySet());
            writeDataToFile(array);
            List<String> values = new ArrayList<>(map.values());
            writeDataToFile(values);

            Set<String> hashSet = new HashSet<>(map.keySet());
            System.out.println(hashSet.size());
            writeDataToFile(hashSet);

            System.out.println(map.containsKey("string1"));
            System.out.println(map.size());
            map.remove("string3");
            writeDataToFile(map);

            // Выводим содержимое HashMap
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataToFile(Map<String, String> map) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write("\n");
            for (Map.Entry<String, String> entry : map.entrySet()) {
               fileWriter.write(entry.getKey() + "->" + entry.getValue() + System.lineSeparator());
            }
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataToFile(List<String> array) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write("\n");
            fileWriter.write(array.toString() + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataToFile(Set<String> hashSet) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write("\n");
            fileWriter.write(hashSet.size());
            fileWriter.write(hashSet.toString() + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}