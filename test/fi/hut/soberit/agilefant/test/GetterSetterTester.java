package fi.hut.soberit.agilefant.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.util.Pair;

@SuppressWarnings("unchecked")
public class GetterSetterTester<T> {

    private final Class<T> targetClass;

    private final T target;

    private static final Map<Class<?>, Object> testValues = new HashMap<Class<?>, Object>();

    private static final Set<String> exceptions = new HashSet<String>();

    static {
        testValues.put(String.class, "TestString");
        testValues.put(Integer.class, 1);
        testValues.put(Integer.TYPE, 1);
        testValues.put(Long.class, 2L);
        testValues.put(Long.TYPE, 2L);
        testValues.put(Boolean.class, Boolean.TRUE);
        testValues.put(Boolean.TYPE, Boolean.TRUE);
        testValues.put(List.class, new ArrayList());
        testValues.put(Collection.class, new ArrayList());
        testValues.put(Set.class, new HashSet());
        testValues.put(Map.class, new HashMap());
        exceptions.add("getClass");
    }

    
    public static <T> GetterSetterTester<T> getInstance(T target) {
        return new GetterSetterTester(target);
    }
    
    public GetterSetterTester(T target) {
        this.target = target;
        targetClass = (Class<T>) target.getClass();
    }
    
    public <ValType> void addTestValue(Pair<Class<ValType>, ? extends ValType> pair) {
        testValues.put(pair.first, pair.second);
    }
    
    public void addExceptions(String... newExceptions) {
        for (String exception : newExceptions) {
            exceptions.add(exception);
        }
    }
    
    public void testGettersAndSetters() {
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                if (exceptions.contains(method.getName()))
                    continue;
                StringBuilder fieldName = new StringBuilder();
                fieldName
                        .append(method.getName().substring(3, 4).toLowerCase());
                fieldName.append(method.getName().substring(4));
                Field field;
                try {
                    field = targetClass.getDeclaredField(fieldName.toString());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to access field "
                            + targetClass.getName() + "." + fieldName, e);
                }
                field.setAccessible(true);
                Object generatedValue = testValues.get(field.getType());
                if (generatedValue == null) {
                    try {
                        generatedValue = field.getType().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Failed to autogenerate a value for field: "
                                        + targetClass.getName() + "."
                                        + fieldName);
                    }
                }
                try {
                    field.set(target, generatedValue);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set field "
                            + targetClass.getName() + "." + fieldName);
                }
                try {
                    if (!method.invoke(target).equals(generatedValue)) {
                        throw new RuntimeException(
                                "Getter did not work as expected: "
                                        + targetClass.getName() + "."
                                        + method.getName());
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke getter "
                            + targetClass.getName() + "." + method.getName());
                }
            }
        }
    }

}
