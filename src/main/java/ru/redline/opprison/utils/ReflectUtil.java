package ru.redline.opprison.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class ReflectUtil {

    @SneakyThrows
    public static <T> T getValue(Object obj, String fieldName) {
        T result = null;
        Field field = getField(obj.getClass(), fieldName);
        field.setAccessible(true);
        result = getValue(obj, field);
        field.setAccessible(false);
        return result;
    }

    public static <T> T getValue(Object obj, Field field) {
        T value = null;
        try {
            field.setAccessible(true);
            value = (T) field.get(obj);
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
        return value;
    }

    public static <T> boolean setValue(Object obj, String fieldName, T value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T> boolean setValue(Object obj, Field field, T value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @SneakyThrows
    public static Field getField(Class<?> clazz, String field) {
        return clazz.getDeclaredField(field);
    }

    public static List<Field> getFields(Object obj) {
        return getFields(obj.getClass());
    }

    public static List<Field> getFields(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    public static List<Class<?>> getClasses(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredClasses());
    }

    public static List<Class<?>> getClasses(Object obj) {
        return getClasses(obj.getClass());
    }

    public static Field findField(Class<?> clazz, Class<?> fieldType) {
        val fields = findFields(clazz, fieldType);
        return fields.isEmpty() ? null : fields.get(0);
    }

    public static List<Field> findFields(Class<?> clazz, Class<?> fieldType) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getType() == fieldType).collect(Collectors.toList());
    }

    public static <T> FieldAccessor<T> fieldAccessor(final Field f) {
        Objects.requireNonNull(f);

        return new FieldAccessor<T>() {
            public T get(Object obj) {
                if (hasField(obj))
                    return ReflectUtil.getValue(obj, f);
                return null;
            }

            public void set(Object obj, Object value) {
                if (hasField(obj))
                    ReflectUtil.setValue(obj, f, value);
            }

            public boolean hasField(Object obj) {
                return Arrays.asList(obj.getClass().getDeclaredFields()).contains(f);
            }
        };
    }

    public interface FieldAccessor<T> {
        T get(Object obj);

        void set(Object obj, Object value);

        boolean hasField(Object obj);
    }
}
