package engine;

import editor.Editor;
import editor.EditorUtil;
import math.Matrix4x4;
import math.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameObject {
    private static final GameObject master = new GameObject(false);
    private static final List<GameObject> instances = new ArrayList<GameObject>();
    private static int h;
    private final Vector2 position = new Vector2(0, 0);
    private final Vector2 scale = new Vector2(1, 1);
    private final List<GameObject> children = new ArrayList<GameObject>();
    private final List<LogicBehaviour> components = new ArrayList<LogicBehaviour>();
    private final String id;
    private final Matrix4x4 matrix = new Matrix4x4();
    public boolean enabled = true;
    public String tag = "Untagged";
    public String name = "New GameObject";
    private float rotation = 0;
    private GameObject parent;
    private int inline = 0;
    private byte dirty = 1;
    private boolean expanded = false;
    private int i;

    public GameObject(boolean addToHierarchy) {
        id = UUID.randomUUID().toString();
        if (!addToHierarchy) {
            inline = -1;
            expanded = true;
            return;
        }

        instances.add(this);
        Parent(master);
    }

    public GameObject(String name) {
        id = UUID.randomUUID().toString();
        this.name = name;
        instances.add(this);
        Parent(master);
    }

    public static GameObject Master() {
        return master;
    }

    static void Clear() {
        instances.clear();
        master.children.clear();
    }

    public static GameObject Find(String s) {
        for (h = 0; h < instances.size(); h++) {
            GameObject g = instances.get(h);
            if (g.name.equals(s)) return g;
        }
        return null;
    }

    public static void PrepareObjects() {
        if (Editor.IsPlaying()) {
            for (h = 0; h < instances.size(); h++) {
                GameObject g = instances.get(h);
                g.Update();
            }
        }

        for (h = 0; h < instances.size(); h++) {
            GameObject g = instances.get(h);
            g.PrepareToRender();
        }
    }

    public final boolean isDirty() {
        return dirty == 1;
    }

    public void UpdateMatrix() {
        matrix.SetTransformation(position, rotation);
    }

    public final Matrix4x4 Matrix() {
        return matrix;
    }

    public final Vector2 Position() {
        return position;
    }

    public void Position(Vector2 v) {
        position.Set(v);
        dirty = 1;
    }

    public void Position(float x, float y) {
        position.x = x;
        position.y = y;
        dirty = 1;
    }

    public final Vector2 Scale() {
        return scale;
    }

    public void Scale(Vector2 v) {
        scale.Set(v);
    }

    public void Scale(float x, float y) {
        scale.x = x;
        scale.y = y;
    }

    public final float Rotation() {
        return rotation;
    }

    public void Rotation(float v) {
        rotation = v;
        dirty = 1;
    }

    public GameObject Parent() {
        return parent;
    }

    public void Parent(GameObject g) {
        if (g == null) g = master;
        if (parent != null) {
            if (parent != g) parent.RemoveChild(this);
            else return;
        }

        parent = g;
        parent.children.add(this);
        SetInline(parent.inline + 1);
    }

    public final List<LogicBehaviour> GetComponents() {
        return components;
    }

    public LogicBehaviour GetComponent(String s) {
        for (i = 0; i < components.size(); i++) {
            LogicBehaviour b = components.get(i);
            if (b.Name().equals(s)) return b;
        }
        return null;
    }

    public void RemoveComponent(LogicBehaviour b) {
        for (i = 0; i < components.size(); i++) {
            if (components.get(i) == b) {
                components.remove(i);
                return;
            }
        }
    }

    public void RemoveComponent(String s) {
        for (i = 0; i < components.size(); i++) {
            if (components.get(i).Name().equals(s)) {
                components.remove(i);
                return;
            }
        }
    }

    public LogicBehaviour AddComponent(LogicBehaviour b) {
        if (b == null) {
            Debug.Log("Could not find behaviour!");
            return null;
        }
        b.Init(this);
        components.add(b);
        return b;
    }

    public LogicBehaviour AddComponent(String v) {
        Class<?> cls;

        try {
            try {
                cls = Class.forName(v);
            } catch (NoClassDefFoundError e) {
                return null;
            }
            try {
                return AddComponent((LogicBehaviour) cls.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                Debug.Log("Instance of " + v + " could not be instantiated!");
            }

        } catch (ClassNotFoundException e) {
            LogicBehaviour l = EditorUtil.GetBehaviour(v);
            if (l != null) return AddComponent(l);
            Debug.Log("Class " + v + " could not be found!");
        }
        return null;
    }

    public String ID() {
        return id;
    }

    public void Expand(boolean b) {
        expanded = b;
    }

    public boolean Expanded() {
        return expanded;
    }

    public int Inline() {
        return inline;
    }

    public void SetInline(int v) {
        inline = v;
        for (i = 0; i < children.size(); i++) children.get(i).SetInline(v + 1);
    }

    public List<GameObject> Children() {
        return children;
    }

    public void RemoveChild(GameObject g) {
        for (i = 0; i < children.size(); i++) {
            if (children.get(i) == g) {
                RemoveChild(i);
                return;
            }
        }
    }

    public void RemoveChild(int v) {
        if (v >= children.size()) return;
        GameObject g = children.get(v);
        g.Parent(master);
        children.remove(v);
        g.inline = 0;
    }

    public void Update() {
        for (i = 0; i < components.size(); i++) {
            LogicBehaviour component = components.get(i);
            component.Update();
        }
    }

    public void PrepareToRender() {
        for (i = 0; i < components.size(); i++) {
            LogicBehaviour component = components.get(i);
            if (component.Name().equals("SpriteRenderer")) Renderer.AddToRenderer((SpriteRenderer) component);
        }
    }
}
