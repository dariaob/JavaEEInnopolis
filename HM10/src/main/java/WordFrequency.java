import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordFrequency {
    public static String getMostFrequentWords(String lines, int n)  {
            return Stream.of(lines)
                    // Разделяем строки на слова (последовательности букв и цифр)
                    .flatMap(line -> Stream.of(line.split("[^a-zA-Z0-9а-яА-Я]+")))
                    // Приводим слова к нижнему регистру
                    .map(String::toLowerCase)
                    // Фильтруем пустые строки
                    .filter(word -> !word.isEmpty())
                    // Группируем слова по их частоте
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
                    // Преобразуем Map в Stream записей (слово -> частота)
                    .entrySet().stream()
                    // Сортируем записи сначала по частоте (по убыванию), затем по слову (лексикографически)
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                            .thenComparing(Map.Entry.comparingByKey()))
                    // Ограничиваем количество записей
                    .limit(n)
                    // Извлекаем слова
                    .map(Map.Entry::getKey)
                    // Собираем результат в список
                    .collect(Collectors.joining(" "));
        }
    }

