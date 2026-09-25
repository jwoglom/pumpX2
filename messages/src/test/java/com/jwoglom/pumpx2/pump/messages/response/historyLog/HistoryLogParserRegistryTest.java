package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Guards against HistoryLog subclasses that exist but are missing from
 * {@link HistoryLogParser#LOG_MESSAGE_TYPES}, which makes the parser return an
 * {@link UnknownHistoryLog} for every record of that opcode.
 */
public class HistoryLogParserRegistryTest {
    /**
     * Concrete, {@link HistoryLogProps}-annotated HistoryLog subclasses that are deliberately
     * left out of {@link HistoryLogParser#LOG_MESSAGE_TYPES}, mapped to the reason. Empty today.
     */
    private static final Map<Class<? extends HistoryLog>, String> NOT_REGISTERED = Map.of();

    @Test
    public void testEveryAnnotatedHistoryLogIsRegistered() throws Exception {
        List<String> missing = annotatedHistoryLogClasses().stream()
                .filter(c -> !HistoryLogParser.LOG_MESSAGE_TYPES.contains(c))
                .filter(c -> !NOT_REGISTERED.containsKey(c))
                .map(c -> c.getAnnotation(HistoryLogProps.class).opCode() + " " + c.getSimpleName())
                .collect(Collectors.toList());

        assertEquals("add to HistoryLogParser.LOG_MESSAGE_TYPES, or to NOT_REGISTERED with a reason",
                List.of(), missing);
    }

    @Test
    public void testNotRegisteredEntriesAreActuallyUnregistered() {
        for (Class<? extends HistoryLog> clazz : NOT_REGISTERED.keySet()) {
            assertFalse(clazz.getSimpleName() + " is registered, remove it from NOT_REGISTERED",
                    HistoryLogParser.LOG_MESSAGE_TYPES.contains(clazz));
        }
    }

    @Test
    public void testRegisteredTypeIdsMatchAnnotationAndAreUnique() throws Exception {
        Map<Integer, Class<? extends HistoryLog>> seen = new HashMap<>();
        for (Class<? extends HistoryLog> clazz : HistoryLogParser.LOG_MESSAGE_TYPES) {
            HistoryLogProps props = clazz.getAnnotation(HistoryLogProps.class);
            assertNotNull(clazz.getSimpleName() + " has no @HistoryLogProps", props);

            int typeId = clazz.getDeclaredConstructor().newInstance().typeId();
            assertEquals(clazz.getSimpleName() + " typeId() differs from @HistoryLogProps.opCode",
                    props.opCode(), typeId);

            Class<? extends HistoryLog> previous = seen.put(typeId, clazz);
            assertTrue("typeId " + typeId + " is used by both " + previous + " and " + clazz, previous == null);
            assertEquals(clazz, HistoryLogParser.LOG_MESSAGE_IDS.get(typeId));
        }
        assertEquals(HistoryLogParser.LOG_MESSAGE_TYPES.size(), HistoryLogParser.LOG_MESSAGE_IDS.size());
        assertEquals(HistoryLogParser.LOG_MESSAGE_TYPES.size(), HistoryLogParser.LOG_MESSAGE_CLASS_TO_ID.size());
    }

    @Test
    public void testClassScanFindsTheHistoryLogPackage() throws Exception {
        List<Class<? extends HistoryLog>> found = annotatedHistoryLogClasses();
        assertTrue(found.contains(BolusDeliveryHistoryLog.class));
        assertTrue(found.contains(CgmHgaSettingsHistoryLog.class));
        assertFalse(found.contains(UnknownHistoryLog.class));
        assertTrue(found.size() >= HistoryLogParser.LOG_MESSAGE_TYPES.size());
    }

    /**
     * Every concrete HistoryLog subclass annotated with {@link HistoryLogProps} in the
     * historyLog package, read from the compiled classes directory or jar.
     */
    @SuppressWarnings("unchecked")
    static List<Class<? extends HistoryLog>> annotatedHistoryLogClasses() throws Exception {
        String pkg = HistoryLog.class.getPackage().getName();
        String pkgPath = pkg.replace('.', '/');
        URL location = HistoryLog.class.getProtectionDomain().getCodeSource().getLocation();
        File root = new File(location.toURI());

        List<String> simpleNames = new ArrayList<>();
        if (root.isDirectory()) {
            File[] files = new File(root, pkgPath).listFiles();
            assertNotNull("no compiled classes found under " + pkgPath, files);
            for (File f : files) {
                simpleNames.add(f.getName());
            }
        } else {
            try (JarFile jar = new JarFile(root)) {
                for (JarEntry entry : jar.stream().collect(Collectors.toList())) {
                    String name = entry.getName();
                    if (name.startsWith(pkgPath + "/") && name.indexOf('/', pkgPath.length() + 1) < 0) {
                        simpleNames.add(name.substring(pkgPath.length() + 1));
                    }
                }
            }
        }

        List<Class<? extends HistoryLog>> ret = new ArrayList<>();
        for (String fileName : simpleNames) {
            if (!fileName.endsWith(".class") || fileName.contains("$")) {
                continue;
            }
            Class<?> clazz = Class.forName(pkg + "." + fileName.substring(0, fileName.length() - ".class".length()));
            if (HistoryLog.class.isAssignableFrom(clazz)
                    && !Modifier.isAbstract(clazz.getModifiers())
                    && clazz.isAnnotationPresent(HistoryLogProps.class)) {
                ret.add((Class<? extends HistoryLog>) clazz);
            }
        }
        ret.sort(Comparator.comparing(Class::getName));
        return ret;
    }
}
