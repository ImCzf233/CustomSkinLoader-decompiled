package customskinloader.utils;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

public class ReflectionUtils
{
    private ReflectionUtils() {
    }
    
    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... parameterTypes) throws NoSuchMethodException {
        final Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (DataType.compare(constructor.getParameterTypes(), primitiveTypes)) {
                constructor.setAccessible(true);
                return constructor;
            }
        }
        throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
    }
    
    public static Object instantiateObject(final Class<?> clazz, final Object... arguments) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
    }
    
    public static Method getMethod(final Class<?> clazz, final boolean declared, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {
        final Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        final Method[] array;
        final Method[] methods = array = (declared ? clazz.getDeclaredMethods() : clazz.getMethods());
        for (final Method method : array) {
            if (method.getName().equals(methodName) && DataType.compare(method.getParameterTypes(), primitiveTypes)) {
                if (declared) {
                    method.setAccessible(true);
                }
                return method;
            }
        }
        throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
    }
    
    public static Object invokeMethod(final Object instance, final boolean declared, final String methodName, final Object... arguments) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return getMethod(instance.getClass(), declared, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
    }
    
    public static Object invokeMethod(final Object instance, final Class<?> clazz, final boolean declared, final String methodName, final Object... arguments) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return getMethod(clazz, declared, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
    }
    
    public static Field getField(final Class<?> clazz, final boolean declared, final String fieldName) throws NoSuchFieldException {
        final Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
        field.setAccessible(true);
        return field;
    }
    
    public static Object getValue(final Object instance, final Class<?> clazz, final boolean declared, final String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = getField(clazz, declared, fieldName);
        return field.get(instance);
    }
    
    public static Object getValue(final Object instance, final boolean declared, final String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getValue(instance, instance.getClass(), declared, fieldName);
    }
    
    public static void setValue(final Object instance, final Class<?> clazz, final boolean declared, final String fieldName, final Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field field = getField(clazz, declared, fieldName);
        field.set(instance, value);
    }
    
    public static void setValue(final Object instance, final boolean declared, final String fieldName, final Object value) throws NoSuchFieldException, IllegalAccessException {
        setValue(instance, instance.getClass(), declared, fieldName, value);
    }
    
    public enum DataType
    {
        BYTE((Class<?>)Byte.TYPE, (Class<?>)Byte.class), 
        SHORT((Class<?>)Short.TYPE, (Class<?>)Short.class), 
        INTEGER((Class<?>)Integer.TYPE, (Class<?>)Integer.class), 
        LONG((Class<?>)Long.TYPE, (Class<?>)Long.class), 
        FLOAT((Class<?>)Float.TYPE, (Class<?>)Float.class), 
        DOUBLE((Class<?>)Double.TYPE, (Class<?>)Double.class), 
        CHARACTER((Class<?>)Character.TYPE, (Class<?>)Character.class), 
        BOOLEAN((Class<?>)Boolean.TYPE, (Class<?>)Boolean.class);
        
        private static final Map<Class<?>, DataType> CLASS_MAP;
        private final Class<?> primitive;
        private final Class<?> reference;
        
        private DataType(final Class<?> primitive, final Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }
        
        public Class<?> getPrimitive() {
            return this.primitive;
        }
        
        public Class<?> getReference() {
            return this.reference;
        }
        
        public static DataType fromClass(final Class<?> clazz) {
            return DataType.CLASS_MAP.get(clazz);
        }
        
        public static Class<?> getPrimitive(final Class<?> clazz) {
            final DataType type = fromClass(clazz);
            return (type != null) ? type.getPrimitive() : clazz;
        }
        
        public static Class<?> getReference(final Class<?> clazz) {
            final DataType type = fromClass(clazz);
            return (type != null) ? type.getReference() : clazz;
        }
        
        public static Class<?>[] getPrimitive(final Class<?>[] classes) {
            final int length = (classes != null) ? classes.length : 0;
            final Class<?>[] types = (Class<?>[])new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = getPrimitive(classes[index]);
            }
            return types;
        }
        
        public static Class<?>[] getReference(final Class<?>[] classes) {
            final int length = (classes != null) ? classes.length : 0;
            final Class<?>[] types = (Class<?>[])new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = getReference(classes[index]);
            }
            return types;
        }
        
        public static Class<?>[] getPrimitive(final Object[] objects) {
            final int length = (objects != null) ? objects.length : 0;
            final Class<?>[] types = (Class<?>[])new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = getPrimitive(objects[index].getClass());
            }
            return types;
        }
        
        public static Class<?>[] getReference(final Object[] objects) {
            final int length = (objects != null) ? objects.length : 0;
            final Class<?>[] types = (Class<?>[])new Class[length];
            for (int index = 0; index < length; ++index) {
                types[index] = getReference(objects[index].getClass());
            }
            return types;
        }
        
        public static boolean compare(final Class<?>[] primary, final Class<?>[] secondary) {
            if (primary == null || secondary == null || primary.length != secondary.length) {
                return false;
            }
            for (int i = 0; i < primary.length; ++i) {
                final Class<?> primaryClass = primary[i];
                final Class<?> secondaryClass = secondary[i];
                if (!primaryClass.equals(secondaryClass) && !primaryClass.isAssignableFrom(secondaryClass)) {
                    return false;
                }
            }
            return true;
        }
        
        static {
            CLASS_MAP = new HashMap<Class<?>, DataType>();
            for (final DataType type : values()) {
                DataType.CLASS_MAP.put(type.primitive, type);
                DataType.CLASS_MAP.put(type.reference, type);
            }
        }
    }
}
