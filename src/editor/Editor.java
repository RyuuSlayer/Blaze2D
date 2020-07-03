package editor;

import engine.*;
import gui.GUI;
import gui.GUISkin;
import gui.GUIStyle;
import gui.Sprite;
import math.Vector2;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Editor {
    public static final String workspaceDirectory = System.getProperty("user.dir") + "/blaze2d-workspace/";
    public static final String editorVersion = "0.1";
    public static GUISkin skin;
    public static GUIStyle arrowDown;
    public static GUIStyle arrowRight;
    public static GUIStyle window;
    private static byte configInit = 0;
    private static String workingDirectory = "";

    private static Hierarchy h;
    private static Inspector i;
    private static ProjectPanel p;
    private static MenuBar m;
    private static GameObject selected;
    private static Object selectedAsset;
    private static Object inspected;
    private static byte playing = 0;

    public static void InitConfig() {
        if (configInit == 1) return;
        configInit = 1;
        File configFile = new File(workspaceDirectory + "config.properties");

        try {
            FileReader r = new FileReader(configFile);
            Properties p = new Properties();
            p.load(r);

            String recordedVersion = p.getProperty("Version");
            if (!editorVersion.equals(recordedVersion)) {
                System.out.println("Version Changed");
            } else System.out.println("Same Version");

            r.close();
        } catch (FileNotFoundException e) {
            System.out.println("New User");
            SaveConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Init() {
        skin = GUISkin.GetSkin("DefaultGUI");
        assert skin != null;
        arrowDown = skin.Get("ArrowDown");
        arrowRight = skin.Get("ArrowRight");
        window = skin.Get("Window");

        h = new Hierarchy();
        i = new Inspector();
        p = new ProjectPanel();
        m = new MenuBar();
    }

    public static boolean IsPlaying() {
        return playing == 1;
    }

    public static void Play(boolean shouldPlay) {
        if (shouldPlay) {
            try {
                SaveScene(SceneManager.CurrentScene());
                playing = 1;
            } catch (IOException e) {
                Debug.Log("Could not save current scene. Play mode could not be started!");
            }
        } else {
            SceneManager.LoadScene(SceneManager.CurrentScene());
            SetSelected(null);
            playing = 0;
        }
    }

    public static void Render() {
        //Prepare the gui for rendering
        GUI.Prepare();

        Debug.Draw();
        m.Render();

        if (playing == 0) {
            GUI.Window(new Rect(0, 30, 400, Application.Height() - 260), "Hierarchy", h::Render, window);
            GUI.Window(new Rect(Application.Width() - 400, 30, 400, Application.Height() - 260), "Inspector", i::Render, window);
            GUI.Window(new Rect(0, Application.Height() - 230, 400, 200), "Asset Types", p::RenderTypes, window);
            GUI.Window(new Rect(400, Application.Height() - 230, Application.Width() - 400, 200), "Assets", p::RenderAssets, window);
        }

        //Unbind the gui
        GUI.Unbind();
    }

    public static Object GetInspected() {
        return inspected;
    }

    private static void SetInspected(Object o) {
        inspected = o;
        if (o != null) i.SetAttributes(o);
    }

    public static GameObject GetSelected() {
        return selected;
    }

    public static void SetSelected(GameObject g) {
        selected = g;
        SetInspected(g);
    }

    public static Object GetSelectedAsset() {
        return selectedAsset;
    }

    public static void SetSelectedAsset(Object o) {
        selectedAsset = o;
        SetInspected(o);
    }

    public static void SaveConfig(File f) {
        try {
            File dir = new File(workspaceDirectory);
            dir.mkdir();

            Properties p = new Properties();
            p.setProperty("Version", editorVersion);

            FileWriter writer = new FileWriter(f);
            p.store(writer, "Logic Configuration");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void OpenProject(String name) {
        Application.name += " -" + name + "-";
        workingDirectory = workspaceDirectory + name + "/";

        File projDir = new File(workingDirectory);
        boolean newProject = projDir.mkdir();

        new File(workingDirectory + "Font/").mkdir();
        new File(workingDirectory + "Materials/").mkdir();
        new File(workingDirectory + "Shaders/").mkdir();
        new File(workingDirectory + "Skins/").mkdir();
        new File(workingDirectory + "Sprites/").mkdir();
        new File(workingDirectory + "Textures/").mkdir();
        new File(workingDirectory + "Scenes/").mkdir();
        new File(workingDirectory + "Scripts/").mkdir();

        //Put opening of project stuff here
    }

    public static String WorkingDirectory() {
        return workingDirectory;
    }

    public static void SaveScene(String sceneName) throws IOException {
        File f = new File(workingDirectory + "Scenes/" + sceneName + ".scene");
        FileWriter fw = new FileWriter(f);

        List<GameObject> objectList = new ArrayList<>();

        GameObject master = GameObject.Master();
        objectList.add(master);
        while (objectList.size() > 0) {
            GameObject g = objectList.get(0);
            List<GameObject> children = g.Children();

            if (children.size() > 0) {
                for (GameObject child : children) objectList.add(0, child);
            }
            if (g == master) {
                objectList.remove(objectList.size() - 1);
                continue;
            }

            WriteTransform(g, fw);

            List<LogicBehaviour> b = g.GetComponents();
            for (LogicBehaviour logicBehaviour : b) WriteComponent(logicBehaviour, fw);
            if (g.Parent() != master) fw.write("\t<P Name=\"" + g.Parent().name + "\">\n</G>\n");
            else fw.write("</G>\n");
            objectList.remove(g);
        }

        fw.close();
    }

    private static void WriteTransform(GameObject g, FileWriter fw) throws IOException {
        String line = "<G Name=\"" + g.name + "\" ";
        line += "Position=\"" + g.Position().x + " " + g.Position().x + "\" ";
        line += "Scale=\"" + g.Scale().x + " " + g.Scale().x + "\" ";
        line += "Rotation=\"" + g.Rotation() + "\">\n";
        fw.write(line);
    }

    private static void WriteComponent(LogicBehaviour b, FileWriter fw) throws IOException {
        Class<?> c = b.getClass();
        String line = "\t<C Name\"" + c.getCanonicalName() + "\">\n";
        fw.write(line);

        Field[] fields = c.getFields();

        for (Field f : fields) {
            line = "\t\t<V " + f.getName() + "=\"";

            try {
                String[] t = f.getType().toString().split("\\.");
                if (t[t.length - 1].equals("Vector2")) {
                    Vector2 v = (Vector2) f.get(b);
                    line += v.x + " " + v.y;
                } else if (t[t.length - 1].equals("Sprite")) line += ((Sprite) f.get(b)).name;
                else line += f.get(b).toString();
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            fw.write(line + "\">\n");
        }
    }

    public static Hierarchy hierarchy() {
        return h;
    }

    public static Inspector inspector() {
        return i;
    }

    public static ProjectPanel projectPanel() {
        return p;
    }
}
