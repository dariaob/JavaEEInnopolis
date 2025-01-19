import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Integer> intList = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader("./app/input.txt"))) {
            String line = reader.readLine();
            String[] data = line.replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",");


            for (String num : data) {
                intList.add(Integer.valueOf(num));
            }

            // Сортировка в натуральном порядке
            intList.sort(Comparator.naturalOrder());
            System.out.println(intList);
            writeDataToFile(intList, data);

            // Сортировка в обратном порядке
            intList.sort(Comparator.reverseOrder());
            System.out.println(intList);
            writeDataToFile(intList);

            // Массив из списка
            Integer[] neoArray = intList.toArray(new Integer[0]);
            System.out.println(neoArray);
            writeDataToFile(neoArray);

            // Перемешивание список
            Collections.shuffle(intList);
            System.out.println(intList);
            writeDataToFile(intList);

            // Сдвиг на 1
            Collections.rotate(intList, 1);
            System.out.println(intList);
            writeDataToFile(intList);

            // Только оригинальные
            List<Integer> uniqueIntList = intList.stream().distinct().collect(Collectors.toList());
            uniqueIntList.sort(Comparator.naturalOrder());
            System.out.println(uniqueIntList);
            writeDataToFile(uniqueIntList);

            // Только дубликаты
            Set<Integer> set = intList.stream()
                    .filter(i -> Collections.frequency(intList, i) > 1)
                    .collect(Collectors.toSet());
            List<Integer> duplicates = new ArrayList<>(set);
            duplicates.sort(Comparator.naturalOrder());
            System.out.println(duplicates);
            writeDataToFile(duplicates);

        }

        // HashSet
        try (BufferedReader readers =
                     new BufferedReader(new FileReader("./app/inputSet.txt"))) {
            String readLine = readers.readLine();
            // Создаем сет
            List<String> words = List.of(readLine.split(","));
            Set<String> stringSet = new HashSet<>(words);
            System.out.println(stringSet);
            writeSetToFile(stringSet);

            stringSet.add("яблоко");
            stringSet.add("один");
            stringSet.add("два");
            stringSet.add("три");
            stringSet.add("пять");
            writeSetToFile(stringSet);

            // Выводим на экран элементы
            for (String word: stringSet) {
                System.out.println(word);
            }

            System.out.println(stringSet.contains("три"));

            stringSet.remove("пять");
            writeSetToFile(stringSet);

            System.out.println(stringSet.size());
            stringSet.add("яблоко");

            // Удаление всех объектов
            Set<String> copySet = new HashSet<>(stringSet);
            stringSet.removeAll(copySet);
            System.out.println(stringSet.isEmpty());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void writeDataToFile(List<Integer> numbers, String[] strings) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write(Arrays.toString(strings));
            fileWriter.write("\n");
            fileWriter.write(numbers.toString() + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataToFile(List<Integer> numbers) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write("\n");
            fileWriter.write(numbers.toString() + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataToFile(Integer[] neoArray) {
        try(FileWriter fileWriter = new FileWriter("./app/output.txt", true)) {
            fileWriter.write("\n");
            fileWriter.write(Arrays.toString(neoArray) + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeSetToFile(Set<String> set) {
        try(FileWriter fileWriter = new FileWriter("./app/outputSet.txt", true)) {
            fileWriter.write(set.toString() + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
