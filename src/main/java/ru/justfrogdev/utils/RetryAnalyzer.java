package ru.justfrogdev.utils;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class RetryAnalyzer implements IRetryAnalyzer, IAnnotationTransformer {
    private static final ConcurrentHashMap<String, Integer> retryCountMap = new ConcurrentHashMap<>();
    private static final int maxRetryCount = 2;

    @Override
    public boolean retry(ITestResult result) {
        String testName = result.getName();
        int retryCount = retryCountMap.getOrDefault(testName, 0);

        if (retryCount < maxRetryCount) {
            retryCountMap.put(testName, retryCount + 1);
            return true;
        }
        retryCountMap.remove(testName);
        return false;
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
} 