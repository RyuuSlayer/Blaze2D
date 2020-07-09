package engine;

import editor.Editor;
import math.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Material {
    // Static variables used for the materials list
    private static final List<Material> materials = new ArrayList<>();
    private static int i;
    public String name;
    public Texture texture;
    public Color color;
    public Shader shader;
    private File f = null;
    private long lastModified;

    // Create a texture on the fly
    public Material(String n, Texture t, Color c, Shader s) {
        // Set all the information to the info passed in
        name = n;
        texture = t;
        color = c;
        shader = s;
    }

    // Import a material by name
    public Material(String name) {
        // Create a buffered reader variable
        BufferedReader br;

        try {
            // Set the buffered reader by passing in the file reader for the path of the material in the folder
            if (name.startsWith("/")) {
                InputStreamReader isr = new InputStreamReader(Material.class.getResourceAsStream("/Materials" + name + ".Material"));
                br = new BufferedReader(isr);
                this.name = name.replaceFirst("/", "");
            } else {
                f = new File(name + ".Material");
                lastModified = f.lastModified();
                br = new BufferedReader(new FileReader(f));
                String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
                this.name = split[split.length - 1];
            }

            // Set the texture this material will be using
            texture = Texture.Find(br.readLine().split(" ")[1]);

            // Cache the color of this material
            String[] o = br.readLine().split(" ")[1].split(",");
            color = new Color(Float.parseFloat(o[0]), Float.parseFloat(o[1]), Float.parseFloat(o[2]), Float.parseFloat(o[3]));

            // Set the shader we will be using
            shader = Shader.Find(br.readLine().split(" ")[1]);

            // add the material to the list of materials
            materials.add(this);

            // And close the buffered reader
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get a material by name
    public static Material Get(String name) {
        // For all the materials
        for (i = 0; i < materials.size(); i++) {
            // If we have came across the material were looking for, return it
            if (materials.get(i).name.equals(name)) return materials.get(i);
        }

        // If we didn't find a material by that name, return null
        return null;
    }

    public static void RefreshAll() {
        for (i = 0; i < materials.size(); i++) {
            try {
                materials.get(i).Refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Material> Materials() {
        return materials;
    }

    public static void Create(String name) throws IOException {
        File src = Objects.requireNonNull(Get("DefaultGame")).f;
        File dest = new File(Editor.WorkingDirectory() + "Materials/" + name + ".Material");

        try (FileInputStream fis = new FileInputStream(src); FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) fos.write(buffer, 0, length);
        }

        new Material(dest.getAbsolutePath().split("\\.")[0]);
    }

    private void Refresh() throws IOException {
        if (f == null) return;
        File temp = new File(f.getAbsolutePath());
        if (!temp.exists()) return;

        if (temp.lastModified() == lastModified) return;
        f = temp;
        lastModified = f.lastModified();

        BufferedReader br = new BufferedReader(new FileReader(f));

        // Set the texture this material will be using
        texture = Texture.Find(br.readLine().split(" ")[1]);

        // Cache the color of this material
        String[] o = br.readLine().split(" ")[1].split(",");
        color = new Color(Float.parseFloat(o[0]), Float.parseFloat(o[1]), Float.parseFloat(o[2]), Float.parseFloat(o[3]));

        // Set the shader we will be using
        shader = Shader.Find(br.readLine().split(" ")[1]);

        // And close the buffered reader
        br.close();
    }

    // Bind the material
    public void Bind() {
        // Bind the shader and texture
        shader.Bind();
        texture.Bind();
    }

    // Unbind the material
    public void Unbind() {
        // Unbind the shader and texture
        shader.Unbind();
        texture.Unbind();
    }
}