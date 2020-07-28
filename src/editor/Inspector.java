package editor;

import engine.*;
import gui.*;
import math.Vector2;

import java.lang.Object;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Inspector {
    private final List<BehaviourAttributes> a = new ArrayList<>();
    private int i = 0;
    private int offsetY = 0;
    private int scroll = 0;
    private final GUIStyle window;

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

            selected.Name(GUI.TextField(new Rect(0, 0, r.width, 22), "GameObject", selected.Name(), 100));
            //Setting local instead of global now
            selected.LocalPosition(GUI.VectorField(new Rect(0, 24, r.width, 22), "Position", selected.LocalPosition(), 100));
            selected.LocalScale(GUI.VectorField(new Rect(0, 48, r.width, 22), "Scale", selected.LocalScale(), 100));
            selected.LocalRotation(GUI.FloatField(new Rect(0, 72, r.width, 22), "Rotation", selected.LocalRotation(), 100));

            offsetY = 96;
            for (i = 0; i < a.size(); i++) {
                BehaviourAttributes att = a.get(i);

                float h = att.height + (window.padding.y + window.padding.height);
                GUI.Window(new Rect(0, offsetY, r.width, h), ((LogicBehaviour) a.get(i).behaviour).Name() + ".Java", this::DrawVariables, window);
                offsetY += h + 2;
            }

            if (GUI.Button("+ Add Component +", new Rect(0, offsetY, r.width, 26), "Button", "ButtonHover")) {
                //String output = tinyfd_inputBox("Add Component", "What component would you like to add?", "");
                String output = Dialog.InputDialog("Add Component", "");
                if (output == null) return;

                LogicBehaviour l = selected.AddComponent(output);
                if (l != null) SetAttributes(selected);
				
				/*
				Class<?> cls;
				try
				{
					cls = Class.forName("game." + output);
					
					try
					{
						try {selected.AddComponent((LogicBehaviour)cls.getConstructor().newInstance());}
						catch (IllegalArgumentException e) {e.printStackTrace();}
						catch (InvocationTargetException e) {e.printStackTrace();}
						catch (NoSuchMethodException e) {e.printStackTrace();}
						catch (SecurityException e) {e.printStackTrace();}
						
						SetAttributes(selected);
					}
					catch (InstantiationException | IllegalAccessException e){
						tinyfd_messageBox("Could not add component!", "The component you are trying to add does not exist in the game package", "okcancel", "Error", true);
					}
				}
				catch (ClassNotFoundException e)
				{
					tinyfd_messageBox("Could not add component!", "The component you are trying to add does not exist in the game package", "okcancel", "Error", true);
				}*/
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
                case "Sprite":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            Sprite sprite = (Sprite) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], sprite, Sprite.class, 100);
                            s.set(ba.behaviour, o);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "GameObject":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            GameObject go = (GameObject) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], go, GameObject.class, 100);
                            s.set(ba.behaviour, o);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Texture":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            Texture tex = (Texture) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], tex, Texture.class, 100);
                            s.set(ba.behaviour, o);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "GUISkin":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            GUISkin skin = (GUISkin) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], skin, GUISkin.class, 100);
                            s.set(ba.behaviour, o);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Font":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            Font font = (Font) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], font, Font.class, 100);
                            s.set(ba.behaviour, o);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Material":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            Material mat = (Material) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], mat, Material.class, 100);
                            s.set(ba.behaviour, o);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Shader":
                    try {
                        Field s = ba.c.getDeclaredField(split[0]);
                        try {
                            Shader shader = (Shader) s.get(ba.behaviour);
                            engine.Object o = GUI.ObjectField(new Rect(0, f * 22 + padding, r.width, 22), split[0], shader, Shader.class, 100);
                            s.set(ba.behaviour, o);
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
                BehaviourAttributes b = new BehaviourAttributes(o);
                if (b != null) a.add(new BehaviourAttributes(l.get(i)));
            }
            return;
        }
        BehaviourAttributes b = new BehaviourAttributes(o);
        if (b != null) a.add(b);
    }
}