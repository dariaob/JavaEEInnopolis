import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Stream<Integer> grayCodeStream = GrayCode.cycleGrayCode(2);
        grayCodeStream.forEach(System.out::println);

        // Пути к файлам
        Path inputPath = Paths.get("./app/input.txt");
        Path outputPath = Paths.get("./app/output.txt");
        // Количество наиболее часто встречающихся слов для вывода
        int n =10;
        // Чтение текста из файла, подсчет слов и запись результата
        // Читаем все строки из файла
        List<String> lines = Files.readAllLines(inputPath);

        // Обрабатываем каждую строку как отдельный пример
        List<String> results = lines.stream()
                .map(line -> WordFrequency.getMostFrequentWords(line, n))
                .collect(Collectors.toList());

        // Записываем результат в файл
        Files.write(outputPath, results);
    }
}
