import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Predicate<Object> condition = Objects::isNull;
        Function<Object,Integer> ifTrue = obj->0;
        Function<CharSequence,Integer> ifFalse = CharSequence::length;

        Function<String,Integer> safeStringLength = ternaryOperator(condition,ifTrue,ifFalse);

        System.out.println(safeStringLength.apply(null));
        System.out.println(safeStringLength.apply("Роза"));
    }

    public static <T> Function<T, Integer> ternaryOperator(Predicate<Object> condition,
                                                           Function<Object, Integer> ifTrue,
                                                           Function<CharSequence, Integer> ifFalse) {
        return (T obj) -> condition.test(obj) ? ifTrue.apply(obj) : ifFalse.apply((CharSequence) obj);
    }
}