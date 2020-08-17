package editor;

import engine.*;
import gui.Font;
import gui.*;
import input.Mouse;
import sound.AudioClip;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectPanel {
    private final GUIStyle box;
    private final GUIStyle empty = null;
    private final Rect temp = new Rect(0, 0, 0, 0);
    private DataType selectedType = DataType.Texture;
    private int i;
    private int scroll1 = 0;
    private int scroll2 = 0;
    private engine.Object selected;

    public ProjectPanel() {
        box = Editor.skin.Get("Box");
    }

    public void RenderTypes(Rect r) {
        DataType[] values = DataType.values();
        scroll1 = GUI.SetScrollView(DataType.values().length * 26, scroll1);
        for (int i = 0; i < values.length; i++) {
            temp.Set(0, i * 26, r.width, 26);
            if (!values[i].equals(selectedType)) {
                if (GUI.Button(values[i].name() + "s", temp, empty, empty)) selectedType = values[i];
            } else {
                GUI.Button(values[i].name() + "s", temp, box, box);
                //Rect s = GUI.Box(temp, box);
                //GUI.BeginArea(s);
                //GUI.Label(values[i].name() + "s", 0, 0);
                //GUI.EndArea();
            }
        }
    }

    public void Popup(String s) {
        if (s == null) return;

        if (s.equals("New Asset")) {
            if (selectedType == DataType.Shader) {
                try {
                    Shader.Create("New Shader");
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot duplicate default shader asset!");
                }
            }
            if (selectedType == DataType.Material) {
                try {
                    Material.Create("New Material");
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot duplicate default material asset!");
                }
            }
            if (selectedType == DataType.Skin) {
                try {
                    GUISkin.Create("New Skin");
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot duplicate default skin asset!");
                }
            }
            if (selectedType == DataType.Sprite) {
                try {
                    Sprite.Create("New Sprite");
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot duplicate default skin asset!");
                }
            }
        }
    }

    private void RenderAssetType(Rect r, engine.Object asset, String folder, String ext, byte containsMouse) throws CloneNotSupportedException {
        if (selected != null) {
            if (asset.equals(selected)) {
                if (GUI.Button(asset.Name(), r, box, box)) {
                    if (Mouse.MultiClicked()) {
                        File f = new File(Editor.WorkingDirectory() + folder + asset.Name() + ext);
                        if (f.exists()) {
                            try {
                                Desktop.getDesktop().open(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Editor.SetSelectedAsset(asset.clone());
                }
            } else if (GUI.Button(asset.Name(), r, empty, empty)) Editor.SetSelectedAsset(asset.clone());
        } else if (GUI.Button(asset.Name(), r, empty, empty)) Editor.SetSelectedAsset(asset.clone());
        if (GUI.checkDrag == 1 && containsMouse == 1) {
            GUI.checkDrag = 0;
            Editor.SetDraggableObject(asset);
        }
    }

    public void RenderAssets(Rect r) throws CloneNotSupportedException {
        Byte containsMouse = 0;

        if (r.Contains(Mouse.Position())) {
            containsMouse = 1;
            if (Mouse.GetButtonDown(1)) {
                List<String> v = new ArrayList<String>();
                if (selectedType != DataType.Font && selectedType != DataType.Texture && selectedType != DataType.Scene && selectedType != DataType.AudioClip) {
                    v.add("New Asset");
                    GUI.SetPopup(new Rect(Mouse.Position().x - 10, Mouse.Position().y - 10, 10, 10), v, this::Popup);
                }
            }
        }

        selected = Editor.GetSelectedAsset();

        if (selectedType == DataType.Font) {
            List<Font> fonts = Font.Fonts();
            scroll2 = GUI.SetScrollView(fonts.size() * 26, scroll2);
            for (i = 0; i < fonts.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), fonts.get(i), "Font/", ".ttf", containsMouse);
            }
        } else if (selectedType == DataType.Material) {
            List<Material> materials = Material.Materials();
            scroll2 = GUI.SetScrollView(materials.size() * 26, scroll2);
            for (i = 0; i < materials.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), materials.get(i), "Materials/", ".Material", containsMouse);
            }
        } else if (selectedType == DataType.Shader) {
            List<Shader> shaders = Shader.Shaders();
            scroll2 = GUI.SetScrollView(shaders.size() * 26, scroll2);
            for (i = 0; i < shaders.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), shaders.get(i), "Shaders/", ".Shader", containsMouse);
            }
        } else if (selectedType == DataType.Skin) {
            List<GUISkin> skins = GUISkin.Skins();
            scroll2 = GUI.SetScrollView(skins.size() * 26, scroll2);
            for (i = 0; i < skins.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), skins.get(i), "Skins/", ".Skin", containsMouse);
            }
        } else if (selectedType == DataType.Sprite) {
            List<Sprite> sprites = Sprite.Sprites();
            scroll2 = GUI.SetScrollView(sprites.size() * 26, scroll2);
            for (i = 0; i < sprites.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), sprites.get(i), "Sprites/", ".Sprite", containsMouse);
            }
        } else if (selectedType == DataType.Texture) {
            List<Texture> textures = Texture.GetTextures();
            scroll2 = GUI.SetScrollView(textures.size() * 26, scroll2);
            for (i = 0; i < textures.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), textures.get(i), "Textures/", "", containsMouse);
            }
        } else if (selectedType == DataType.AudioClip) {
            List<AudioClip> clips = AudioClip.GetClips();
            scroll2 = GUI.SetScrollView(clips.size() * 26, scroll2);
            for (i = 0; i < clips.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), clips.get(i), "Audio/", "", containsMouse);
            }
        }
    }
}
