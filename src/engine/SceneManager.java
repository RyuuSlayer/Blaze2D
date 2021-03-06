package engine;

import editor.Editor;
import gui.Font;
import gui.GUISkin;
import gui.Sprite;
import math.Vector2;
import physics.Collider;
import sound.AudioClip;
import sound.AudioSource;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneManager {
    private static String currentScene = "";

    public static final String CurrentScene() {
        return currentScene;
    }

    public static void LoadScene(String name) {
        currentScene = name;

        BufferedReader br = null;
        if (ProjectSettings.isEditor) {
            FileReader fr;
            try {
                fr = new FileReader(Editor.WorkingDirectory() + "/Scenes/" + name + ".scene");
            } catch (FileNotFoundException e) {
                Debug.Log("Scene " + name + " wasnt loaded because it could not be found");
                return;
            }
            br = new BufferedReader(fr);
        } else {
            InputStreamReader isr = new InputStreamReader(SceneManager.class.getResourceAsStream("/Scenes/" + name + ".scene"));
            br = new BufferedReader(isr);
        }

        Collider.Clear();
        Camera.Clear();
        GameObject.Clear();
        AudioSource.CleanUp();

        String line;

        try {
            GameObject g = null;
            LogicBehaviour b = null;

            Map<LogicBehaviour, List<String>> batch = new HashMap<LogicBehaviour, List<String>>();
            List<String> currentInfo = null;
            while ((line = br.readLine()) != null) {
                line = line.replace("\t", "");
                String[] split = line.split("\"");

                //New Way to create gameobjects after parenting
                if (line.startsWith("<G")) g = CreateObject(split);
                else if (line.startsWith("<B")) {
                    b = g.AddComponent(split[1]);
                    currentInfo = new ArrayList<String>();
                } else if (line.startsWith("</B>")) {
                    batch.put(b, currentInfo);
                    currentInfo = null;
                } else if (currentInfo != null) currentInfo.add(line);
                else if (line.startsWith("<P")) g.Parent(GameObject.Find(split[1]));
            }

            for (LogicBehaviour behaviour : batch.keySet()) SetClass(behaviour, batch.get(behaviour));

            GameObject.Recalculate();

            List<GameObject> gos = GameObject.Instances();
            for (GameObject go : gos) {
                go.ResetDirty();
                go.Start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static engine.Object SetClass(engine.Object o, List<String> data) {
        String bLine = "";
        while (data.size() > 0) {
            bLine = data.get(0);

            if (bLine.startsWith("<A")) {
                String[] sep = bLine.split("<A ")[1].split("=");

                try {
                    data.remove(0);
                    SetArray(o, sep[0], data, Integer.parseInt(sep[1].split("\"")[1]));
                    continue;
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    Debug.Log("Could not set " + sep[0]);
                }
            }
            if (bLine.startsWith("<V")) {
                String[] sep = bLine.split("<V ")[1].split("=");
                try {
                    SetVariable(o, sep[0], sep[1].split("\"")[1]);
                    data.remove(0);
                    continue;
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    Debug.Log("Could not set " + sep[0]);
                }
            } else if (bLine.startsWith("<C")) {
                String sep = bLine.split("<C ")[1].split("=")[0];
                try {
                    Field f = o.getClass().getField(sep);
                    CustomClass cached = (CustomClass) f.get(o).getClass().getConstructor().newInstance();
                    data.remove(0);
                    f.set(o, SetClass(cached, data));
                    continue;
                } catch (NoSuchFieldException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                    System.out.println(o.getClass().getCanonicalName() + " Does not contain " + sep);
                    e.printStackTrace();
                    return o;
                }
            } else if (bLine.startsWith("</C>")) {
                data.remove(0);
                return o;
            }
        }
        return o;
    }

    private static GameObject CreateObject(String[] split) {
        GameObject g = new GameObject(split[1]);

        String[] tSplit = split[3].split(" ");
        g.Position(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));

        tSplit = split[5].split(" ");
        g.Scale(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));

        g.Rotation(Float.parseFloat(split[7]));

        g.depth = Float.parseFloat(split[9]);

        g.SetLayer(Integer.parseInt(split[11]));

        g.instanceID(split[13]);

        return g;
    }

    private static void SetVariable(engine.Object o, String field, String input) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (o == null) return;

        Class<?> c = o.getClass();
        if (c == null) return;

        Field f = c.getField(field);
        if (f == null) return;

        String[] splitName = f.getType().toString().split("\\.");
        String dataType = splitName[splitName.length - 1];

        if (dataType.equals("String")) {
            f.set(o, input);
            return;
        } else if (dataType.equals("float")) {
            f.set(o, Float.parseFloat(input));
            return;
        } else if (dataType.equals("int")) {
            f.set(o, Integer.parseInt(input));
            return;
        } else if (dataType.equals("boolean")) {
            f.set(o, Boolean.valueOf(input));
            return;
        } else if (dataType.equals("Vector2")) {
            String[] split = input.split(" ");
            f.set(o, new Vector2(Float.parseFloat(split[0]), Float.parseFloat(split[1])));
            return;
        } else if (dataType.equals("Sprite")) {
            f.set(o, Sprite.Get(input));
            return;
        } else if (dataType.equals("AudioClip")) {
            f.set(o, AudioClip.Find(input));
            return;
        } else if (dataType.equals("GUISkin")) {
            f.set(o, GUISkin.GetSkin(input));
            return;
        } else if (dataType.equals("Texture")) {
            f.set(o, Texture.Find(input));
            return;
        } else if (dataType.equals("Font")) {
            f.set(o, Font.Find(input));
            return;
        } else if (dataType.equals("Material")) {
            f.set(o, Material.Get(input));
            return;
        } else if (dataType.equals("Shader")) {
            f.set(o, Shader.Find(input));
            return;
        } else if (dataType.equals("GameObject")) f.set(o, GameObject.FindObjectByID(input));
        else if (LogicBehaviour.class.isAssignableFrom(f.getType())) {
            GameObject go = GameObject.FindObjectByID(input);
            if (go != null) f.set(o, go.GetComponent(dataType));
        }
    }

    private static void SetArray(engine.Object o, String fieldName, List<String> data, int size) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = o.getClass().getField(fieldName);
        if (f == null) return;

        Class<?> type = f.getType().getComponentType();

        if (type == int.class) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                String s = data.get(0);
                array[i] = Integer.parseInt(s.split("<V ")[1].split("=")[1].split("\"")[1]);
                data.remove(0);
            }
            f.set(o, array);
        }
    }
}
