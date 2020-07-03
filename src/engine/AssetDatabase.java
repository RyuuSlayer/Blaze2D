package engine;

import editor.Editor;
import editor.EditorUtil;
import gui.Font;
import gui.GUISkin;
import gui.Sprite;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AssetDatabase {
    private static final Class<AssetDatabase> clazz = AssetDatabase.class;
    private static final ClassLoader cl = clazz.getClassLoader();
    private static List<String> textures = new ArrayList<>();
    private static List<String> fonts = new ArrayList<>();
    private static List<String> shaders = new ArrayList<>();
    private static List<String> materials = new ArrayList<>();
    private static List<String> sprites = new ArrayList<>();
    private static List<String> skins = new ArrayList<>();
    private static List<String> scripts = new ArrayList<>();

    private static int i;

    public static void LoadAllResources() {
        InitResourcePaths();

        for (i = 0; i < textures.size(); i++) new Texture(textures.get(i));
        for (i = 0; i < fonts.size(); i++) new Font(fonts.get(i), 16);
        for (i = 0; i < shaders.size(); i++) new Shader(shaders.get(i));
        for (i = 0; i < materials.size(); i++) new Material(materials.get(i));
        for (i = 0; i < sprites.size(); i++) new Sprite(sprites.get(i));
        for (i = 0; i < skins.size(); i++) new GUISkin(skins.get(i));

        for (i = 0; i < scripts.size(); i++) EditorUtil.ImportClass(scripts.get(i));
    }

    private static void InitResourcePaths() {
        URL dirURL = cl.getResource("engine");
        assert dirURL != null;
        String protocol = dirURL.getProtocol();

        if (protocol.equals("file")) ImportFromDirectory();
        else {
            try {
                ImportFromJar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void ImportFromDirectory() {
        textures = ImportFromLocalDirectory("Textures", 1);
        fonts = ImportFromLocalDirectory("Font", 0);
        shaders = ImportFromLocalDirectory("Shaders", 0);
        materials = ImportFromLocalDirectory("Materials", 0);
        sprites = ImportFromLocalDirectory("Sprites", 0);
        skins = ImportFromLocalDirectory("Skins", 0);

        scripts = ImportFromExternalDirectory("Scripts", 1);

        textures.addAll(ImportFromExternalDirectory("Textures", 1));
        fonts.addAll(ImportFromExternalDirectory("Font", 0));
        shaders.addAll(ImportFromExternalDirectory("Shaders", 0));
        materials.addAll(ImportFromExternalDirectory("Materials", 0));
        sprites.addAll(ImportFromExternalDirectory("Sprites", 0));
        skins.addAll(ImportFromExternalDirectory("Skins", 0));
    }

    private static List<String> ImportFromLocalDirectory(String path, int useExtension) {
        List<String> paths = new ArrayList<>();
        InputStream in = cl.getResourceAsStream(path);
        assert in != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;

        try {
            while ((line = br.readLine()) != null) {
                if (useExtension == 1) paths.add("/" + line);
                else paths.add("/" + line.split("\\.")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return paths;
    }

    private static List<String> ImportFromExternalDirectory(String path, int useExtension) {
        List<String> paths = new ArrayList<>();
        File dir = new File(Editor.WorkingDirectory() + path);
        File[] files = dir.listFiles();
        for (i = 0; i < Objects.requireNonNull(files).length; i++) {
            String aPath = files[i].getAbsolutePath();
            if (path.endsWith("/")) continue;
            if (useExtension == 0) aPath = aPath.split("\\.")[0];
            paths.add(aPath);
        }
        return paths;
    }

    private static void ImportFromJar() throws IOException {
        String me = clazz.getName().replace(".", "/") + ".class";
        URL dirURL = cl.getResource(me);

        assert dirURL != null;
        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith("Textures")) {
                    textures.add(name.split("/")[1]);
                } else if (name.startsWith("Font")) {
                    fonts.add(name.split("/")[1].split("\\.")[0]);
                } else if (name.startsWith("Materials")) {
                    materials.add(name.split("/")[1].split("\\.")[0]);
                } else if (name.startsWith("Shaders")) {
                    shaders.add(name.split("/")[1].split("\\.")[0]);
                } else if (name.startsWith("Sprites")) {
                    sprites.add(name.split("/")[1].split("\\.")[0]);
                } else if (name.startsWith("Skins")) {
                    skins.add(name.split("/")[1].split("\\.")[0]);
                }
            }
            jar.close();
        }
    }
}
