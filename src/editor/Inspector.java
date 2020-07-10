package editor;

import engine.Debug;
import engine.GameObject;
import engine.LogicBehaviour;
import engine.Rect;
import gui.GUI;
import gui.GUIStyle;
import math.Vector2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_inputBox;

public class Inspector {
    private final List<BehaviourAttributes> a = new ArrayList<>();
    private final GUIStyle window;
    private int i = 0;
    private int offsetY = 0;
    private int scroll = 0;

    public Inspector() {
        window = Editor.skin.Get("Window");
    }

    public void Render(Rect r) {
        Object inspected = Editor.GetInspected();
        if (inspected == null) return;

        if (inspected instanceof GameObject) {
            int addition = 122;
            for (i = 0; i < a.size(); i++) addition += a.get(i).height + (window.padding.y + window.padding.height) + 2;
            scroll = GUI.SetScrollView(addition, scroll);

            GameObject selected = (GameObject) inspected;

            selected.name = GUI.TextField(new Rect(0, 0, r.width, 22), "GameObject", selected.name, 100);
            selected.Position(GUI.VectorField(new Rect(0, 24, r.width, 22), "Position", selected.Position(), 100));
            selected.Scale(GUI.VectorField(new Rect(0, 48, r.width, 22), "Scale", selected.Scale(), 100));
            selected.Rotation(GUI.FloatField(new Rect(0, 72, r.width, 22), "Rotation", selected.Rotation(), 100));

            offsetY = 96;
            for (i = 0; i < a.size(); i++) {
                BehaviourAttributes att = a.get(i);

                float h = att.height + (window.padding.y + window.padding.height);
                GUI.Window(new Rect(0, offsetY, r.width, h), ((LogicBehaviour) a.get(i).behaviour).Name() + ".Java", this::DrawVariables, window);
                offsetY += h + 2;
            }

            if (GUI.Button("+ Add Component +", new Rect(0, offsetY, r.width, 26), "Button", "ButtonHover")) {
                String output = tinyfd_inputBox("Add Component", "What component would you like to add?", "");

                if (output == null) return;

                LogicBehaviour l = selected.AddComponent(output);
                if (l != null) SetAttributes(selected);

            }
            offsetY = 0;
        } else {
            i = 0;
            GUI.Window(new Rect(0, offsetY, r.width, a.get(0).height + (window.padding.y + window.padding.height)), a.get(0).behaviour.getClass().getSimpleName(), this::DrawVariables, window);
            if (GUI.CenteredButton("Save", new Rect(0, offsetY + (a.get(0).height + (window.padding.y + window.padding.height)) + 2, r.width, 26), "Button", "ButtonHover")) {

            }
        }
    }

    public void DrawVariables(Rect r) {
        BehaviourAttributes ba = a.get(i);

        String[] fields = a.get(i).fields;
        int padding = 0;
        for (int f = 0; f < fields.length; f++) {
            String[] split = fields[f].split(" ");

            switch (split[1]) {
                case "String":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            String p = s.get(ba.behaviour).toString();
                            String v = GUI.TextField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100);
                            if (!p.equals(v)) s.set(ba.behaviour, v);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "float":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            String p = s.get(ba.behaviour).toString();
                            float fl = Float.parseFloat(p);
                            float v = GUI.FloatField(new Rect(0, f * 22 + padding, r.width, 22), split[0], fl, 100);
                            if (!p.equals(String.valueOf(v))) s.set(ba.behaviour, v);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "int":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            String p = s.get(ba.behaviour).toString();
                            String v = GUI.TextField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100);
                            if (!p.equals(v)) {
                                try {
                                    s.set(ba.behaviour, Integer.parseInt(v));
                                } catch (NumberFormatException e) {
                                    Debug.Log("Input is not in a number format! Rejecting value.");
                                }
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Vector2":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            Vector2 p = (Vector2) s.get(ba.behaviour);
                            Vector2 v = GUI.VectorField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100);
                            if (!p.equals(v)) {
                                s.set(ba.behaviour, v);
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    GUI.TextField(new Rect(0, f * 22 + padding, r.width, 22), split[0], "", 100);
                    break;
            }
            padding += 2;
        }
    }

    public void SetAttributes(Object o) {
        scroll = 0;
        a.clear();

        if (o instanceof GameObject) {
            List<LogicBehaviour> l = ((GameObject) o).GetComponents();
            for (i = 0; i < l.size(); i++) {
                a.add(new BehaviourAttributes(l.get(i)));
            }
            return;
        }
        BehaviourAttributes b = new BehaviourAttributes(o);
        a.add(b);
    }
}