package com.jwoglom.pumpx2.shared;

import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import org.apache.commons.codec.binary.Hex;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Stream;


public class JavaHelpers {
    public static Map<String, Object> getProperties(final Object bean) {
        final Map<String, Object> result = new HashMap<>();

        try {
            final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                final Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null) {
                    result.put(propertyDescriptor.getName(), readMethod.invoke(bean, (Object[]) null));
                }
            }
        } catch (Exception ex) {
            // ignore
        }

        return result;
    }

    public static String display(final Object obj) {
        if (obj == null) {
            return "null";
        }

        String out;
        if (obj.getClass() == byte[].class) {
            out = Hex.encodeHexString((byte[]) obj);
        } else {
            out = obj.toString();
        }

        // Remove null byte
        StringJoiner joiner = new StringJoiner("");
        Stream.of(out.split("\0")).forEach(joiner::add);

        return joiner.toString();
    }
}
