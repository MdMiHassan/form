package mdmihassan.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Preconditions {

    public static boolean isNullOrBlank(String str) {
        return isNullOrBlankInternal(str);
    }

    public static boolean nonNullAndNonBlank(String str) {
        return !isNullOrBlankInternal(str);
    }

    public static boolean nonNullAndNonEmpty(Object object) {
        return !isNullOrEmptyInternal(object);
    }

    private static boolean isNullOrBlankInternal(String str) {
        return str == null || str.isBlank();
    }

    public static boolean isNullOrEmpty(Object object) {
        return isNullOrEmptyInternal(object);
    }

    public static <T> T requireNonNullAndNonEmptyOrElse(T object, T alternative) {
        if (isNullOrEmptyInternal(object)) {
            return alternative;
        }
        return object;
    }

    public static <T> T requireNonNullAndNonEmptyOrElseGet(T object, Supplier<T> alternativeSupplier) {
        if (isNullOrEmptyInternal(object)) {
            return alternativeSupplier.get();
        }
        return object;
    }

    private static boolean isNullOrEmptyInternal(Object object) {
        return switch (object) {
            case null -> true;
            case CharSequence cs -> cs.isEmpty();
            case Collection<?> collection -> collection.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            case Optional<?> optional -> optional.isEmpty();
            case Object[] array -> array.length == 0;
            case Iterable<?> iterable -> !iterable.iterator().hasNext();
            default -> false;
        };
    }

    public static String requireNonNullAndNonBlankOrElseThrow(String str, String message) {
        if (isNullOrBlank(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

}
