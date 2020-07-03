package engine;

import editor.Editor;
import gui.Sprite;
import math.Vector2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

public class SceneManager {
    private static String currentScene = "";

    public static String CurrentScene() {
        return currentScene;
    }

    public static void LoadScene(String name) {
        currentScene = name;

        FileReader fr;
        try {
            fr = new FileReader(Editor.WorkingDirectory() + "/Scenes/" + name + ".scene");
        } catch (FileNotFoundException e) {
            Debug.Log("Scene " + name + " was not loaded because it could not be found");
            return;
        }

        GameObject.Clear();
        BufferedReader br = new BufferedReader(fr);
        String line;

        try {
            GameObject g = null;
            LogicBehaviour b = null;

            while ((line = br.readLine()) != null) {
                line = line.replace("\t", "");
                String[] split = line.split("\"");

                if (line.startsWith("<G")) g = CreateObject(split);
                else if (line.startsWith("<C")) {
                    assert g != null;
                    b = g.AddComponent(split[1]);
                } else if (line.startsWith("<V")) {
                    String[] sep = line.split("<V ")[1].split("=");
                    try {
                        assert b != null;
                        SetVariable(b, sep[0], sep[1].split("\"")[1]);
                    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                        Debug.Log("Could not set " + sep[0]);
                    }
                } else if (line.startsWith("<P")) {
                    assert g != null;
                    g.Parent(GameObject.Find(split[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GameObject CreateObject(String[] split) {
        GameObject g = new GameObject(split[1]);

        String[] tSplit = split[3].split(" ");
        g.Position(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));

        tSplit = split[5].split(" ");
        g.Scale(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));

        g.Rotation(Float.parseFloat(split[7]));

        return g;
    }

    private static void SetVariable(LogicBehaviour b, String field, String input) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = b.getClass().getField(field);

        String[] splitName = f.getType().toString().split("\\.");
        String dataType = splitName[splitName.length - 1];

        switch (dataType) {
            case "String" -> f.set(b, input);
            case "int" -> f.set(b, Integer.parseInt(input));
            case "Vector2" -> {
                String[] split = input.split(" ");
                f.set(b, new Vector2(Float.parseFloat(split[0]), Float.parseFloat(split[1])));
            }
            case "Sprite" -> f.set(b, Sprite.Get(input));
        }
    }
}