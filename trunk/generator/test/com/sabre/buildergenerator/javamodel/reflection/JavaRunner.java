package com.sabre.buildergenerator.javamodel.reflection;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class JavaRunner {
    private static final Iterable<String> COMPILER_OPTIONS = Arrays.asList("-source", "1.5");

    private final JavaCompiler compiler;
    private final JavaFileManager fileManager;
    private final DiagnosticCollector<JavaFileObject> diagnostics;

    public JavaRunner() throws IOException {
        compiler = ToolProvider.getSystemJavaCompiler();
        fileManager = createFileManager();
        diagnostics = new DiagnosticCollector<JavaFileObject>();
    }

    public void compileClasses(String... names) throws Exception {
        List<JavaFileObject> sourceFiles = new ArrayList<JavaFileObject>();
        for (String name : names) {
            sourceFiles.add(fileManager.getJavaFileForInput(SOURCE_OUTPUT, name, Kind.SOURCE));
        }
        compiler.getTask(null, fileManager, diagnostics, COMPILER_OPTIONS, null, sourceFiles).call();
        printDiagnostics();
        if (!getDiagnostics().getDiagnostics().isEmpty()) {
            throw new Exception("Compilation errors");
        }
    }

    public void printDiagnostics() {
        for (Diagnostic<? extends JavaFileObject> diag : diagnostics.getDiagnostics()) {
            System.out.println(diag.getMessage(Locale.getDefault()));
        }
    }

    public void close() throws IOException {
        // TODO delete files
        fileManager.close();
    }

    public void addSource(String className, String classSource) throws IOException {
        JavaFileObject jfo = fileManager.getJavaFileForOutput(SOURCE_OUTPUT, className, Kind.SOURCE, null);
        Writer writer = jfo.openWriter();
        writer.write(classSource);
        writer.close();
    }

    private JavaFileManager createFileManager() throws IOException {
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        fileManager.setLocation(SOURCE_OUTPUT, Arrays.asList(new File("bin")));
        fileManager.setLocation(CLASS_OUTPUT, Arrays.asList(new File("bin")));

        return fileManager;
    }

    public void run(String className, String methodName) throws Exception {
        Class<?> thisClass = loadClass(className);
        run(thisClass, methodName);
    }

    public void run(Class<?> clazz, String methodName) throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        Class<?> params[] = {};
        Object paramsObj[] = {};
        Object iClass = clazz.newInstance();
        Method thisMethod = clazz.getDeclaredMethod(methodName, params);
        thisMethod.invoke(iClass, paramsObj);
    }

    public void runMain(Class<?> clazz) throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        Class<?> params[] = { String[].class };
        Object paramsObj[] = { new String[0] };
        Method thisMethod = clazz.getDeclaredMethod("main", params);
        thisMethod.invoke(null, paramsObj);
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return fileManager.getClassLoader(CLASS_OUTPUT).loadClass(className);
    }

    public DiagnosticCollector<JavaFileObject> getDiagnostics() {
        return diagnostics;
    }
}
