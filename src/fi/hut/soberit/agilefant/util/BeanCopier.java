package fi.hut.soberit.agilefant.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class BeanCopier {

    public static <T extends Object>void copy(T source, T destination) {
        try {
            BeanInfo sourceInfo = Introspector.getBeanInfo(source.getClass());
            PropertyDescriptor[] sourceDescriptors = sourceInfo.getPropertyDescriptors();
            
            for (PropertyDescriptor descriptor : sourceDescriptors) {
                String name = descriptor.getName();
                copyAttribute(source, destination, name);
            }
            
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }
    
    private static <T extends Object> void copyAttribute(T source, T dest, String name) {
        PropertyDescriptor reader;
        PropertyDescriptor writer;
        
        if (name.equals("class")) {
            return;
        }
        
        try {
            writer = new PropertyDescriptor(name, dest.getClass());
            reader = new PropertyDescriptor(name, source.getClass());
        
            Object value = reader.getReadMethod().invoke(source);
            Class<?> sourceType = reader.getPropertyType();
            Class<?> destType = writer.getPropertyType();
            if (value == null) {
                writer.getWriteMethod().invoke(dest, new Object[] { null });
            }
            else if (sourceType.getName().equals(destType.getName())) {
                writer.getWriteMethod().invoke(dest, value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
