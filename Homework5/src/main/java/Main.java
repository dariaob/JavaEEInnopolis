import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Integer> intList = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader("hm5/src/main/resources/input.txt"))) {
            String line = reader.readLine();
            String[] data = line.replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",");


            for (String num : data) {
                intList.add(Integer.valueOf(num));
            }

            // Сортировка в натуральном порядке
            intList.sort(Comparator.naturalOrder());
            writeDataToFile(intList, data);

            intList.sort(Comparator.reverseOrder());
            writeDataToFile(intList, data);

            Integer[] neoArray = intList.toArray(new Integer[0]);
            writeDataToFile(intList, data);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public static void writeDataToFile(List<Integer> numbers, String[] strings) {
        try(FileWriter fileWriter = new FileWriter("hm5/src/main/resources/output.txt", true)) {
            fileWriter.write(Arrays.toString(strings));
            fileWriter.write("\n");
            fileWriter.write(numbers.toString() + System.lineSeparator());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
