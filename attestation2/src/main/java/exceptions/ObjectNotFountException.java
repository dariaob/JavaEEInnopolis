package exceptions;

/**
 * Выбрасывается если не найден объект
 */
public class ObjectNotFountException extends Throwable{
    public ObjectNotFountException(String message) {
        super(message);
    }
}
