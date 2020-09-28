package gui;

import engine.Rect;
import engine.Texture;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GUISkin extends engine.Object {
    private static final List<GUISkin> skins = new ArrayList<GUISkin>();
    private static int i;
    public Texture texture;
    private final List<GUIStyle> styles = new ArrayList<GUIStyle>();

    //Import a skin by name
    public GUISkin(String name) {
        //Create a buffered reader variable
        BufferedReader br;

        try {
            //Set the buffered reader by passing in the file reader for the path of the skin in the folder
            if (name.startsWith("/")) {
                InputStreamReader isr = new InputStreamReader(GUISkin.class.getResourceAsStream("/Skins" + name + ".Skin"));
                br = new BufferedReader(isr);
                Name(name.replaceFirst("/", ""));
            } else {
                f = new File(name + ".Skin");
                lastModified = f.lastModified();
                br = new BufferedReader(new FileReader(f));
                String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
                Name(split[split.length - 1]);
            }

            //Find the texture for this skin
            texture = Texture.Find(br.readLine().split(" ")[1]);

            //Read the line and store it, while the line isn't null
            String line = br.readLine();
            while (line != null) {
                //If the line starts with the Name
                if (line.startsWith("Name:")) {
                    //Get the values we need from the following to lines
                    String[] o = br.readLine().split(" ")[1].split(",");
                    String[] p = br.readLine().split(" ")[1].split(",");

                    //Then set the offset and padding values that we acquired above
                    Rect offset = new Rect(Float.parseFloat(o[0]), Float.parseFloat(o[1]), Float.parseFloat(o[2]), Float.parseFloat(o[3]));
                    Rect padding = new Rect(Float.parseFloat(p[0]), Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]));

                    //Then create the style using the name and information we stored and add it to the list of styles
                    GUIStyle style = new GUIStyle(line.split(" ")[1], texture, offset, padding);
                    styles.add(style);
                }
                //Then go on to the next line
                line = br.readLine();
            }
            //And close the buffered reader
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        skins.add(this);
    }

    public static List<GUISkin> Skins() {
        return skins;
    }

    public static GUISkin GetSkin(String name) {
        //For all the styles
        for (i = 0; i < skins.size(); i++) {
            //If we have came across the style were looking for, return it
            if (skins.get(i).Name().equals(name)) return skins.get(i);
        }

        //If we didn't find a style by that name, return null
        return null;
    }

    public static void RefreshAll() {
        for (i = 0; i < skins.size(); i++) {
            try {
                skins.get(i).Refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isInternal() {
        return f == null;
    }

    //Get a guistyle by name
    public GUIStyle Get(String name) {
        //For all the styles
        for (i = 0; i < styles.size(); i++) {
            //If we have came across the style were looking for, return it
            if (styles.get(i).name.equals(name)) return styles.get(i);
        }

        //If we didn't find a style by that name, return null
        return null;
    }

    private void Refresh() throws IOException {
        if (f == null) return;
        File temp = new File(f.getAbsolutePath());
        if (!temp.exists()) return;

        if (temp.lastModified() == lastModified) return;
        f = temp;
        lastModified = f.lastModified();

        BufferedReader br = new BufferedReader(new FileReader(f));

        //Find the texture for this skin
        texture = Texture.Find(br.readLine().split(" ")[1]);

        //Read the line and store it, while the line isn't null
        String line = br.readLine();
        while (line != null) {
            //If the line starts with the Name
            if (line.startsWith("Name:")) {
                //Get the values we need from the following to lines
                String[] o = br.readLine().split(" ")[1].split(",");
                String[] p = br.readLine().split(" ")[1].split(",");

                //Then set the offset and padding values that we acquired above
                Rect offset = new Rect(Float.parseFloat(o[0]), Float.parseFloat(o[1]), Float.parseFloat(o[2]), Float.parseFloat(o[3]));
                Rect padding = new Rect(Float.parseFloat(p[0]), Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]));

                //Then create the style using the name and information we stored and add it to the list of styles
                String styleName = line.split(" ")[1];
                GUIStyle style = Get(styleName);

                if (style != null) {
                    style.offset = offset;
                    style.padding = padding;
                } else {
                    style = new GUIStyle(line.split(" ")[1], texture, offset, padding);
                    styles.add(style);
                }
            }
            //Then go on to the next line
            line = br.readLine();
        }
        //And close the buffered reader
        br.close();
    }
}
