package ru.justfrogdev.listeners;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupIntersectionInterceptor implements IMethodInterceptor {
    private static final String CURRENT_VERSION_PROPERTY = "currentVersion";
    private static final String MIN_VERSION_PREFIX = "minVersion=";

    private static int compareVersions(String v1, String v2) {
        // Считаем версию вида "4.10.16.0.2.8" набором чисел и сравниваем
        try {
            int[] a = Arrays.stream(v1.split("\\."))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();

            int[] b = Arrays.stream(v2.split("\\."))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();

            int maxLen = Math.max(a.length, b.length);
            for (int i = 0; i < maxLen; i++) {
                int ai = i < a.length ? a[i] : 0;
                int bi = i < b.length ? b[i] : 0;
                if (ai != bi) {
                    return Integer.compare(ai, bi);
                }
            }
            return 0;
        } catch (RuntimeException e) {
            return 0;
        }
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        // Получаем группы из системного свойства
        String requiredGroupsProperty = System.getProperty("requiredGroups");

        final boolean hasRequiredGroups = requiredGroupsProperty != null && !requiredGroupsProperty.isEmpty();
        final Set<String> requiredGroups = hasRequiredGroups
                ? Arrays.stream(requiredGroupsProperty.split(","))
                .collect(Collectors.toSet())
                : Set.of();

        String currentVersionRaw = System.getProperty(CURRENT_VERSION_PROPERTY);
        if (currentVersionRaw == null || currentVersionRaw.isBlank()) {
            currentVersionRaw = null;
        }
        final String currentVersion = currentVersionRaw;

        // Разделяем группы, указанные через запятую
        return methods.stream()
                .filter(method -> {
                    Set<String> methodGroups = Set.of(method.getMethod().getGroups());

                    if (hasRequiredGroups && !methodGroups.containsAll(requiredGroups)) {
                        return false;
                    }

                    // Если у теста нет minVersion-group - он не участвует в версии-воротах.
                    if (currentVersion == null) {
                        return true;
                    }

                    // Поддерживаем несколько minVersion=... групп: берём максимальную (самую "строгую").
                    String maxRequiredMinVersion = null;
                    for (String group : methodGroups) {
                        if (group != null && group.startsWith(MIN_VERSION_PREFIX)) {
                            String requiredMin = group.substring(MIN_VERSION_PREFIX.length());
                            if (requiredMin.isBlank()) {
                                continue;
                            }
                            if (maxRequiredMinVersion == null) {
                                maxRequiredMinVersion = requiredMin;
                            } else if (compareVersions(requiredMin, maxRequiredMinVersion) > 0) {
                                maxRequiredMinVersion = requiredMin;
                            }
                        }
                    }

                    // Не нашли minVersion -> тест выполняется.
                    if (maxRequiredMinVersion == null) {
                        return true;
                    }

                    // Правило: если current < requiredMinVersion, то тест не запускать.
                    return compareVersions(currentVersion, maxRequiredMinVersion) >= 0;
                })
                .collect(Collectors.toList());
    }
}