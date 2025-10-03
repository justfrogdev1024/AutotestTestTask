package ru.justfrogdev.listeners;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupIntersectionInterceptor implements IMethodInterceptor {
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        // Получаем группы из системного свойства
        String requiredGroupsProperty = System.getProperty("requiredGroups");
        if (requiredGroupsProperty == null || requiredGroupsProperty.isEmpty()) {
            return methods; // Если группы не указаны, возвращаем все тесты
        }

        // Разделяем группы, указанные через запятую
        Set<String> requiredGroups = Arrays.stream(requiredGroupsProperty.split(","))
                .collect(Collectors.toSet());

        return methods.stream()
                .filter(method -> {
                    Set<String> methodGroups = Set.of(method.getMethod().getGroups());
                    // Проверяем, что метод содержит все требуемые группы
                    return methodGroups.containsAll(requiredGroups);
                })
                .collect(Collectors.toList());
    }
}