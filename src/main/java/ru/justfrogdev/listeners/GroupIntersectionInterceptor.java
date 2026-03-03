package ru.justfrogdev.listeners;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupIntersectionInterceptor implements IMethodInterceptor {
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String requiredGroupsProperty = System.getProperty("requiredGroups");
        String excludeGroupsProperty = System.getProperty("excludeGroups");

        // Если фильтры не указаны, возвращаем все тесты
        if ((requiredGroupsProperty == null || requiredGroupsProperty.isEmpty()) &&
            (excludeGroupsProperty == null || excludeGroupsProperty.isEmpty())) {
            return methods;
        }

        // Парсим требуемые группы (через запятую)
        Set<String> requiredGroups = parseGroups(requiredGroupsProperty);
        
        // Парсим исключаемые группы (через запятую)
        Set<String> excludeGroups = parseGroups(excludeGroupsProperty);

        return methods.stream()
                .filter(method -> {
                    Set<String> methodGroups = Set.of(method.getMethod().getGroups());
                    
                    // Проверка 1: Метод содержит все требуемые группы (если указаны)
                    boolean matchesRequired = requiredGroups.isEmpty() || 
                                             methodGroups.containsAll(requiredGroups);
                    
                    // Проверка 2: Метод НЕ содержит ни одну из исключаемых групп
                    boolean notExcluded = excludeGroups.isEmpty() || 
                                         Collections.disjoint(methodGroups, excludeGroups);
                    
                    return matchesRequired && notExcluded;
                })
                .collect(Collectors.toList());
    }

    private Set<String> parseGroups(String groupsProperty) {
        if (groupsProperty == null || groupsProperty.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(groupsProperty.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}