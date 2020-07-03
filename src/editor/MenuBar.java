package editor;

import engine.*;
import gui.GUI;
import gui.GUIStyle;
import math.Color;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuBar {
    private final GUIStyle play;

    private final GUIStyle box;
    private final GUIStyle stop;
    public Map<String, List<MenuItem>> menu = new LinkedHashMap<>();
    private GUIStyle empty;
    private String selected;

    public MenuBar() {
        box = Editor.skin.Get("Box");
        play = Editor.skin.Get("PlayButton");
        stop = Editor.skin.Get("StopButton");

        Add("File", new MenuItem("New Scene", this::File));
        Add("File", new MenuItem("Open Scene", this::File));
        Add("File", new MenuItem("Save Scene", this::File));
        Add("File", new MenuItem("Quit", this::File));
        Add("Asset", new MenuItem("New GameObject", this::Asset));
    }

    public void Add(String parent, MenuItem item) {
        List<MenuItem> menuItem = menu.computeIfAbsent(parent, k -> new ArrayList<>());

        menuItem.add(item);
    }

    public void Render() {
        float w = Application.Width();
        if (selected != null) {
            if (!GUI.HasPopup()) selected = null;
        }

        GUI.Box(new Rect(0, 0, w, 30), box);

        Color prevColor = GUI.textColor;
        GUI.textColor = Color.white;
        float offset = 0;
        float index = 0;
        for (String m : menu.keySet()) {
            float width = GUI.font.StringWidth(m) + 10;
            Rect nameRect = new Rect(offset - index, 0, width, 30);
            if (selected == null) {
                if (GUI.CenteredButton(m, nameRect, empty, box)) {
                    List<MenuItem> list = menu.get(m);
                    List<String> v = new ArrayList<>();
                    for (MenuItem menuItem : list) v.add(menuItem.name);
                    GUI.SetPopup(nameRect, v, this::Clicked);
                    selected = m;
                }
            } else {
                if (selected.equals(m)) GUI.CenteredButton(m, nameRect, box, box);
                else {
                    if (GUI.CenteredButton(m, nameRect, empty, box)) {
                        List<MenuItem> list = menu.get(m);
                        List<String> v = new ArrayList<>();
                        for (MenuItem menuItem : list) v.add(menuItem.name);
                        GUI.SetPopup(nameRect, v, this::Clicked);
                        selected = m;
                    }
                }
            }
            offset += width;
            index++;
        }

        GUIStyle style = play;
        boolean isPlaying = Editor.IsPlaying();
        if (isPlaying) style = stop;
        if (GUI.Button("", new Rect(offset - index + 4, 8, 16, 16), style, style)) Editor.Play(!isPlaying);

        GUI.textColor = prevColor;
    }

    public void Clicked(String v) {
        List<MenuItem> list = menu.get(selected);
        for (MenuItem item : list) {
            if (item.name.equals(v)) {
                item.Accept();
                return;
            }
        }
    }

    public void File(MenuItem m) {
        if (m.name.equals("New Scene")) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(Editor.WorkingDirectory() + "Scenes/"));
            fc.setFileFilter(new FileNameExtensionFilter("Scene File", "scene"));
            int result = fc.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File temp = fc.getSelectedFile();
                String name = temp.getName();
                if (name.endsWith(".scene")) name = name.replace(".scene", "");
                try {
                    File f = new File(Editor.WorkingDirectory() + "Scenes/" + name + ".scene");
                    f.createNewFile();
                    SceneManager.LoadScene(name);
                } catch (IOException e) {
                    Debug.Log("Could not save new scene at path");
                }
            }
        }
        if (m.name.equals("Open Scene")) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(Editor.WorkingDirectory() + "Scenes/"));
            fc.setFileFilter(new FileNameExtensionFilter("Scene File", "scene"));
            int result = fc.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                SceneManager.LoadScene(fc.getSelectedFile().getName().split("\\.")[0]);
            }
        }
        if (m.name.equals("Save Scene")) {
            try {
                Editor.SaveScene(SceneManager.CurrentScene());
            } catch (IOException e) {
                Debug.Log("Could not save scene: " + SceneManager.CurrentScene());
            }
        }
    }

    public void Asset(MenuItem m) {
        if (m.name.equals("New GameObject")) new GameObject("New GameObject");
    }
}
