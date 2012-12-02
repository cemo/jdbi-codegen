package com.digitolio.jdbi.codegen;

import com.digitolio.jdbi.annotations.CodeGen;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author C.Koc
 */
public class Scanner {
    public Set<Class<?>> scanPackage(String inputPackage, final String ignoreValue) {

        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[classLoadersList.size()])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(inputPackage)));

        Reflections reflections = new Reflections(configuration);

        Set<Class<?>> subTypesOf = reflections.getSubTypesOf(Object.class);
        return FluentIterable
                .from(subTypesOf)
                .filter(new Predicate<Class<?>>() {
                    @Override
                    public boolean apply(Class<?> input) {
                        CodeGen annotation = input.getAnnotation(CodeGen.class);
                        if (annotation != null) {
                            String[] ignores = annotation.ignore();
                            for (String ignore : ignores) {
                                if (ignore.equals(ignoreValue) || ignore.equals(CodeGen.ALL)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }).toImmutableSet();
    }
}
