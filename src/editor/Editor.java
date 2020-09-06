package editor;

import engine.*;
import gui.GUI;
import gui.GUISkin;
import gui.GUIStyle;
import input.Mouse;
import math.Vector2;

import java.io.*;
import java.lang.Object;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Editor {
    public static final String workspaceDirectory = System.getProperty("user.dir") + "/blaze2d-workspace/";
    public static final String editorVersion = "0.53";
    public static GUISkin skin;
    public static GUIStyle arrowDown;
    public static GUIStyle arrowRight;
    public static GUIStyle toggleOff;
    public static GUIStyle toggleOn;
    public static GUIStyle window;
    public static Rect sceneArea = new Rect(0, 0, 0, 0);
    public static Vector2 cameraPosition = new Vector2();
    private static byte configInit = 0;
    private static String workingDirectory = "";

    private static Hierarchy h;
    private static Inspector i;
    private static ProjectPanel p;
    private static MenuBar m;
    private static engine.Object draggedObject;
    private static GameObject selected;
    private static engine.Object selectedAsset;
    private static engine.Object inspected;
    private static byte playing = 0;
    private static byte draggingScene = 0;
    private static Vector2 startDragPoint = new Vector2();
    private static Vector2 startCameraDragPoint = cameraPosition;
    private static int a;

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
        arrowDown = skin.Get("ArrowDown");
        arrowRight = skin.Get("ArrowRight");
        toggleOff = skin.Get("ToggleOff");
        toggleOn = skin.Get("ToggleOn");
        window = skin.Get("Window");

        h = new Hierarchy();
        i = new Inspector();
        p = new ProjectPanel();
        m = new MenuBar();
    }

    public static final boolean IsPlaying() {
        return playing == 1;
    }

    public static void Play(boolean shouldPlay) {
        if (shouldPlay) {
            try {
                SaveScene(SceneManager.CurrentScene());
                GameObject.StartAll();
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
            sceneArea.Set(400, 30, Application.Width() - 800, Application.Height() - 260);
            if (sceneArea.Contains(Mouse.Position())) {
                if (Mouse.GetButtonDown(2)) {
                    startDragPoint = Mouse.Position();
                    startCameraDragPoint = cameraPosition;
                    draggingScene = 1;
                } else if (Mouse.GetButtonDown(0)) {
                    List<GameObject> l = GameObject.Instances();

                    Vector2 halfScreen = new Vector2(Application.Width() * 0.5f, Application.Height() * 0.5f);
                    Vector2 mousePos = Mouse.Position().Sub(halfScreen).Add(new Vector2(cameraPosition.x, -cameraPosition.y));

                    for (a = 0; a < l.size(); a++) {
                        GameObject g = l.get(a);
                        SpriteRenderer sr = (SpriteRenderer) g.GetComponent("SpriteRenderer");
                        if (sr != null) {
                            if (sr.Contains(new Vector2(mousePos.x, -mousePos.y))) {
                                SetSelected(g);
                                break;
                            }
                        }
                    }
                }
            }
            if (draggingScene == 1) {
                Vector2 dragDistance = startDragPoint.Sub(Mouse.Position());
                cameraPosition = startCameraDragPoint.Add(dragDistance.x, -dragDistance.y);
                if (Mouse.GetButtonUp(2)) draggingScene = 0;
            }

            GUI.Window(new Rect(0, 30, 400, Application.Height() - 260), "Hierarchy", h::Render, window);
            GUI.Window(new Rect(Application.Width() - 400, 30, 400, Application.Height() - 260), "Inspector", i::Render, window);
            GUI.Window(new Rect(0, Application.Height() - 230, 400, 200), "Asset Types", p::RenderTypes, window);
            GUI.Window(new Rect(400, Application.Height() - 230, Application.Width() - 400, 200), "Assets", t -> {
                try {
                    p.RenderAssets(t);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }, window);

            if (Mouse.GetButtonUp(0)) draggedObject = null;
            else if (draggedObject != null) GUI.Label(draggedObject.Name(), Mouse.Position());
        }

        //Unbind the gui
        GUI.Unbind();
    }

    public static final engine.Object DraggedObject() {
        return draggedObject;
    }

    public static void SetDraggableObject(engine.Object draggable) {
        draggedObject = draggable;
    }

    public static final Object GetInspected() {
        return inspected;
    }

    private static void SetInspected(engine.Object o) {
        inspected = o;
        if (o != null) i.SetAttributes(o, true);
    }

    public static final GameObject GetSelected() {
        return selected;
    }

    public static void SetSelected(GameObject g) {
        selected = g;
        SetInspected(g);
    }

    public static final engine.Object GetSelectedAsset() {
        return selectedAsset;
    }

    public static void SetSelectedAsset(engine.Object o) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void OpenProject(String name) {
        Application.name += " -" + name + "-";
        workingDirectory = workspaceDirectory + name + "/";

        File projDir = new File(workingDirectory);
        Boolean newProject = projDir.mkdir();

        new File(workingDirectory + "Font/").mkdir();
        new File(workingDirectory + "Materials/").mkdir();
        new File(workingDirectory + "Shaders/").mkdir();
        new File(workingDirectory + "Skins/").mkdir();
        new File(workingDirectory + "Sprites/").mkdir();
        new File(workingDirectory + "Textures/").mkdir();
        new File(workingDirectory + "Scenes/").mkdir();
        new File(workingDirectory + "Scripts/").mkdir();

        if (newProject) return;


        //Put opening of project stuff here
    }

    public static final String WorkingDirectory() {
        return workingDirectory;
    }

    public static void SaveScene(String sceneName) throws IOException {
        File f = new File(workingDirectory + "Scenes/" + sceneName + ".scene");
        FileWriter fw = new FileWriter(f);

        List<GameObject> objectList = new ArrayList<GameObject>();

        GameObject master = GameObject.Master();
        objectList.add(master);
        while (objectList.size() > 0) {
            GameObject g = objectList.get(0);
            List<GameObject> children = g.Children();

            if (children.size() > 0) {
                for (int i = 0; i < children.size(); i++) objectList.add(0, children.get(i));
            }
            if (g == master) {
                objectList.remove(objectList.size() - 1);
                continue;
            }

            WriteTransform(g, fw);

            List<LogicBehaviour> b = g.GetComponents();
            for (int i = 0; i < b.size(); i++) WriteComponent(b.get(i), fw);
            if (g.Parent() != master) fw.write("\t<P Name=\"" + g.Parent().Name() + "\">\n</G>\n");
            else fw.write("</G>\n");
            objectList.remove(g);
        }

        fw.close();
    }

    private static void WriteTransform(GameObject g, FileWriter fw) throws IOException {
        String line = "<G Name=\"" + g.Name() + "\" ";
        line += "Position=\"" + g.Position().x + " " + g.Position().y + "\" ";
        line += "Scale=\"" + g.Scale().x + " " + g.Scale().y + "\" ";
        line += "Rotation=\"" + g.Rotation() + "\" ";
        line += "Depth=\"" + g.depth + "\" ";
        line += "Layer=\"" + g.GetLayer() + "\" ";
        line += "ID=\"" + g.instanceID() + "\">\n";
        fw.write(line);
    }

    //Write a behaviour
    private static void WriteComponent(LogicBehaviour b, FileWriter fw) throws IOException {
        WriteClass(b, fw, "\t", "");
        fw.write("\t</B>\n");
    }

    //Write a class, this method is only called for an attached behaviour and custom classes
    private static void WriteClass(engine.Object o, FileWriter fw, String starter, String fieldName) throws IOException {
        Class<?> c = o.getClass();
        if (o instanceof engine.LogicBehaviour) fw.write("\t<B Name\"" + c.getCanonicalName() + "\">\n");
        else fw.write(starter + "<C " + fieldName + "=\"" + c.getCanonicalName() + "\">\n");

        Field[] fields = c.getFields();

        for (int i = 0; i < fields.length; i++) {
            try {
                WriteVariable(fields[i], o, fw, starter + "\t");
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (o instanceof CustomClass) fw.write(starter + "</C>\n");
    }

    //Write a variable inside a class
    private static void WriteVariable(Field f, Object o, FileWriter fw, String starter) throws IllegalArgumentException, IllegalAccessException, IOException {
        if (f.getType().isArray()) {
			/*
			Class<?> c = f.get(o).getClass();
			Class<?> type = c.getComponentType();
			if(c.getComponentType().isArray()) return;
			//New to add array type to it so we know what kind of array we need to create
			if(type == int.class) {int[] arr = (int[]) f.get(o); fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n"); for(int i = 0; i < arr.length; i++) fw.write(starter + "\t<V Element" + i + "=\"" + arr[i] + "\">\n");}
			else if(type == double.class) {double[] arr = (double[]) f.get(o); fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n"); for(int i = 0; i < arr.length; i++) fw.write(starter + "\t<V Element" + i + "=\"" + arr[i] + "\">\n");}
			else if(type == short.class) {short[] arr = (short[]) f.get(o); fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n"); for(int i = 0; i < arr.length; i++) fw.write(starter + "\t<V Element" + i + "=\"" + arr[i] + "\">\n");}
			else if(type == byte.class) {byte[] arr = (byte[]) f.get(o); fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n"); for(int i = 0; i < arr.length; i++) fw.write(starter + "\t<V Element" + i + "=\"" + arr[i] + "\">\n");}
			else if(type == boolean.class) {boolean[] arr = (boolean[]) f.get(o); fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n"); for(int i = 0; i < arr.length; i++) fw.write(starter + "\t<V Element" + i + "=\"" + arr[i] + "\">\n");}
			else if(type == String.class) {String[] arr = (String[]) f.get(o); fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n"); for(int i = 0; i < arr.length; i++) fw.write(starter + "\t<V Element" + i + "=\"" + arr[i] + "\">\n");}
			else if(!c.getComponentType().isPrimitive())
			{
				Object[] arr = (Object[]) f.get(o);
				fw.write(starter + "<A " + f.getName() + "=\"" + arr.length + "\">\n");
				for(int i = 0; i < arr.length; i++)
				{
					Object current = arr[i];
					if(current instanceof engine.CustomClass)
					{
						WriteClass((engine.Object) current, fw, starter + "\t", "Element" + i);
					}
					else if(current instanceof engine.Object)
					{
						if(f.get(o) == null) fw.write(starter + "\t<V Element" + i + "=\"NULL\">\n");
						else fw.write(starter + "\t<V Element" + i + "=\"" + ((engine.Object) current).Name() + "\">\n");
					}
					else if(current.getClass() == Vector2.class)
					{
						Vector2 v = (Vector2) current;
						fw.write(starter + "\t<V Element" + i + "=\"" + v.x + " " + v.y + "\">\n");
					}
				}
			}
			*/
        } else if (engine.CustomClass.class.isAssignableFrom(f.getType())) {
            WriteClass((engine.Object) f.get(o), fw, starter, f.getName());
        } else if (engine.Object.class.isAssignableFrom(f.getType())) {
            if (f.get(o) == null) fw.write(starter + "<V " + f.getName() + "=\"NULL\">\n");
            else if (GameObject.class.isAssignableFrom(f.getType())) {
                engine.Object go = (engine.Object) f.get(o);
                fw.write(starter + "<V " + f.getName() + "=\"" + go.instanceID() + "\" " + ">\n");
            } else if (LogicBehaviour.class.isAssignableFrom(f.getType())) {
                LogicBehaviour behaviour = (LogicBehaviour) f.get(o);
                fw.write(starter + "<V " + f.getName() + "=\"" + behaviour.gameObject().instanceID() + "\" " + ">\n");
            } else fw.write(starter + "<V " + f.getName() + "=\"" + ((engine.Object) f.get(o)).Name() + "\">\n");
        } else if (f.getType().isPrimitive() || f.getType().getSimpleName().equals("String"))
            fw.write(starter + "<V " + f.getName() + "=\"" + f.get(o) + "\">\n");
        else if (f.getType() == Vector2.class) {
            Vector2 v = (Vector2) f.get(o);
            fw.write(starter + "<V " + f.getName() + "=\"" + v.x + " " + v.y + "\">\n");
        }
    }

    public static final Hierarchy hierarchy() {
        return h;
    }

    public static final Inspector inspector() {
        return i;
    }

    public static final ProjectPanel projectPanel() {
        return p;
    }
}
