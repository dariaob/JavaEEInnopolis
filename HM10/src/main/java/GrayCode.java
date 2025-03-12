import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GrayCode {
    public static Stream<Integer> cycleGrayCode(int n) {
    // Проверка на допустимое значение n
        if (n < 1 || n > 16) {
        throw new IllegalArgumentException("n должно быть в пределах от 1 до 16");
    }

    // Генерация кодов Грея для n битов
    List<Integer> grayCodes = generateGrayCodes(n);

    // Создание бесконечного циклического потока
        return Stream.iterate(0, i -> (i + 1) % grayCodes.size())
            .map(grayCodes::get);
}

// Метод для генерации кодов Грея
private static List<Integer> generateGrayCodes(int n) {
    List<Integer> grayCodes = new ArrayList<>();
    int size = 1 << n; // 2^n

    for (int i = 0; i < size; i++) {
        int grayCode = i ^ (i >> 1); // Формула для кода Грея
        grayCodes.add(grayCode);
    }

    return grayCodes;
}
}
