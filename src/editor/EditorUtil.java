package editor;

import engine.LogicBehaviour;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EditorUtil {
    private static final List<LogicBehaviour> importedClasses = new ArrayList<LogicBehaviour>();
    private static int i;

    public static void ImportClass(String path) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (stream == null) return;

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String seperator = System.getProperty("line.separator");
        String tempProperty = System.getProperty("java.io.tmpdir");

        String[] name = path.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
        Path srcPath = Paths.get(tempProperty, name[name.length - 1]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            Files.write(srcPath, reader.lines().collect(Collectors.joining(seperator)).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        compiler.run(null, null, null, srcPath.toString());
        Path p = srcPath.getParent().resolve(name[name.length - 1].split("\\.")[0] + ".class");

        URL classURL = null;
        try {
            classURL = p.getParent().toFile().toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (classURL == null) return;

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classURL});

        Class<?> myClass = null;
        try {
            myClass = classLoader.loadClass(name[name.length - 1].split("\\.")[0]);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        if (myClass == null) return;

        LogicBehaviour l = null;
        try {
            l = (LogicBehaviour) myClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        if (l == null) return;

        importedClasses.add(l);
    }

    public static LogicBehaviour GetBehaviour(String name) {
        for (i = 0; i < importedClasses.size(); i++) {
            if (importedClasses.get(i).Name().equals(name)) {
                try {
                    return importedClasses.get(i).getClass().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void CleanUp() {
        String path;
        File javaFile;
        File classFile;

        for (i = 0; i < importedClasses.size(); i++) {
            LogicBehaviour l = importedClasses.get(i);
            path = l.getClass().getProtectionDomain().getCodeSource().getLocation() + l.Name();
            javaFile = new File(path + ".java");
            classFile = new File(path + ".class");

            javaFile.delete();
            classFile.delete();
        }
    }
}
