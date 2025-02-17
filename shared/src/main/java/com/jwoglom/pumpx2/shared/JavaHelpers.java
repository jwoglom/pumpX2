package com.jwoglom.pumpx2.shared;

import com.google.common.reflect.ClassPath;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import com.jwoglom.pumpx2.shared.Hex;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaHelpers {
    private static final String TAG = "JavaHelpers";

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

    public static List<String> getClassNamesWithPackage(String pkg) {
        try {
            return ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream()
                    .filter(clazz -> clazz.getPackageName().toLowerCase().startsWith(pkg))
                    .map(ClassPath.ClassInfo::getName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            L.e(TAG, "cannot get class names", e);
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }


    public static String autoToString(Object bean, Set<String> ignoredPropertyNames) {
        if (ignoredPropertyNames == null) {
            ignoredPropertyNames = new HashSet<>();
        }
        try {
            return new ReflectionToStringBuilder(bean, ToStringStyle.SHORT_PREFIX_STYLE)
                    .setExcludeFieldNames(ignoredPropertyNames.toArray(new String[0]))
                    .build();
        } catch (Exception e) {
            L.e(TAG, "Unable to autoToString: " + e);
            return bean.toString();
        }
//        Map<String, Object> properties = getProperties(bean);
//        String propertiesStr = properties.entrySet().stream()
//                .sorted(Comparator.comparing(Map.Entry::getKey))
//                .filter(entry -> ignoredPropertyNames == null || !ignoredPropertyNames.contains(entry.getKey()))
//                .map(entry -> entry.getKey() + "=" + JavaHelpers.display(entry.getValue()))
//                .collect(Collectors.joining(", "));
//        return bean.getClass().getName() + "(" + propertiesStr + ")";
    }

    public static String autoToStringVerbose(Object bean, Set<String> ignoredPropertyNames) {
        try {
            return new ReflectionToStringBuilder(bean, new MultiLineNoHashcodeToStringStyle())
                    .setExcludeFieldNames(ignoredPropertyNames.toArray(new String[0]))
                    .build();
        } catch (Exception e) {
            L.e(TAG, "Unable to autoToStringVerbose: " + e);
            return bean.toString();
        }
    }

    private static final class MultiLineNoHashcodeToStringStyle extends RecursiveToStringStyle {
        MultiLineNoHashcodeToStringStyle() {
            this.setContentStart("[");
            this.setFieldSeparator(System.lineSeparator() + "  ");
            this.setFieldSeparatorAtStart(true);
            this.setUseIdentityHashCode(false);
            this.setContentEnd(System.lineSeparator() + "]");
        }
        private Object readResolve() {
            return ToStringStyle.MULTI_LINE_STYLE;
        }

    }

    public static String autoToStringJson(Object bean, Set<String> ignoredPropertyNames) {
        try {
            return new ReflectionToStringBuilder(bean, ToStringStyle.JSON_STYLE)
                    .setExcludeFieldNames(ignoredPropertyNames.toArray(new String[0]))
                    .build();
        } catch (Exception e) {
            L.e(TAG, "Unable to autoToStringJson: " + e);
            return bean.toString();
        }
    }
}
