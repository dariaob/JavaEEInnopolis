package exceptions;

/**
 * Выбрасывает исключение при удалении,
 * если есть связанные записи
 */
public class ImpossibleToDeleteException extends Throwable {
    public ImpossibleToDeleteException(String message) {
        super(message);
    }
}
