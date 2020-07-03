package editor;

import engine.*;
import gui.Font;
import gui.*;
import input.Mouse;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProjectPanel {
    private final GUIStyle box;
    private final GUIStyle empty = null;
    private final Rect temp = new Rect(0, 0, 0, 0);
    private DataType selectedType = DataType.Texture;
    private int scroll1 = 0;
    private int scroll2 = 0;

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

    public void RenderAssets(Rect r) {
        Object selected = Editor.GetSelectedAsset();

        int i;
        if (selectedType == DataType.Font) {
            List<Font> fonts = Font.Fonts();
            scroll2 = GUI.SetScrollView(fonts.size() * 26, scroll2);
            for (i = 0; i < fonts.size(); i++) {
                if (selected != null) {
                    if (fonts.get(i).equals(selected)) {
                        if (GUI.Button(fonts.get(i).Name(), new Rect(0, i * 26, r.width, 26), box, box)) {
                            if (Mouse.MultiClicked()) {
                                File f = new File(Editor.WorkingDirectory() + "Font/" + fonts.get(i).Name() + ".ttf");
                                if (f.exists()) {
                                    try {
                                        Desktop.getDesktop().open(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Editor.SetSelectedAsset(fonts.get(i));
                        }
                    } else if (GUI.Button(fonts.get(i).Name(), new Rect(0, i * 26, r.width, 26), empty, empty))
                        Editor.SetSelectedAsset(fonts.get(i));
                } else if (GUI.Button(fonts.get(i).Name(), new Rect(0, i * 26, r.width, 26), empty, empty))
                    Editor.SetSelectedAsset(fonts.get(i));
            }
        } else if (selectedType == DataType.Material) {
            List<Material> materials = Material.Materials();
            scroll2 = GUI.SetScrollView(materials.size() * 26, scroll2);
            for (i = 0; i < materials.size(); i++) {
                if (selected != null) {
                    if (materials.get(i).equals(selected)) {
                        if (GUI.Button(materials.get(i).name, new Rect(0, i * 26, r.width, 26), box, box)) {
                            if (Mouse.MultiClicked()) {
                                File f = new File(Editor.WorkingDirectory() + "Materials/" + materials.get(i).name + ".Material");
                                if (f.exists()) {
                                    try {
                                        Desktop.getDesktop().open(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Editor.SetSelectedAsset(materials.get(i));
                        }
                    } else if (GUI.Button(materials.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                        Editor.SetSelectedAsset(materials.get(i));
                } else if (GUI.Button(materials.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                    Editor.SetSelectedAsset(materials.get(i));
            }
        } else if (selectedType == DataType.Shader) {
            List<Shader> shaders = Shader.Shaders();
            scroll2 = GUI.SetScrollView(shaders.size() * 26, scroll2);
            for (i = 0; i < shaders.size(); i++) {
                if (selected != null) {
                    if (shaders.get(i).equals(selected)) {
                        if (GUI.Button(shaders.get(i).name, new Rect(0, i * 26, r.width, 26), box, box)) {
                            if (Mouse.MultiClicked()) {
                                File f = new File(Editor.WorkingDirectory() + "Shaders/" + shaders.get(i).name + ".Shader");
                                if (f.exists()) {
                                    try {
                                        Desktop.getDesktop().open(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Editor.SetSelectedAsset(shaders.get(i));
                        }
                    } else if (GUI.Button(shaders.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                        Editor.SetSelectedAsset(shaders.get(i));
                } else if (GUI.Button(shaders.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                    Editor.SetSelectedAsset(shaders.get(i));
            }
        } else if (selectedType == DataType.Skin) {
            List<GUISkin> skins = GUISkin.Skins();
            scroll2 = GUI.SetScrollView(skins.size() * 26, scroll2);
            for (i = 0; i < skins.size(); i++) {
                if (selected != null) {
                    if (skins.get(i).equals(selected)) {
                        if (GUI.Button(skins.get(i).name, new Rect(0, i * 26, r.width, 26), box, box)) {
                            if (Mouse.MultiClicked()) {
                                File f = new File(Editor.WorkingDirectory() + "Skins/" + skins.get(i).name + ".Skin");
                                if (f.exists()) {
                                    try {
                                        Desktop.getDesktop().open(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Editor.SetSelectedAsset(skins.get(i));
                        }
                    } else if (GUI.Button(skins.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                        Editor.SetSelectedAsset(skins.get(i));
                } else if (GUI.Button(skins.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                    Editor.SetSelectedAsset(skins.get(i));
            }
        } else if (selectedType == DataType.Sprite) {
            List<Sprite> sprites = Sprite.Sprites();
            scroll2 = GUI.SetScrollView(sprites.size() * 26, scroll2);
            for (i = 0; i < sprites.size(); i++) {
                if (selected != null) {
                    if (sprites.get(i).equals(selected)) {
                        if (GUI.Button(sprites.get(i).name, new Rect(0, i * 26, r.width, 26), box, box)) {
                            if (Mouse.MultiClicked()) {
                                File f = new File(Editor.WorkingDirectory() + "Sprites/" + sprites.get(i).name + ".Sprite");
                                if (f.exists()) {
                                    try {
                                        Desktop.getDesktop().open(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Editor.SetSelectedAsset(sprites.get(i));
                        }
                    } else if (GUI.Button(sprites.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                        Editor.SetSelectedAsset(sprites.get(i));
                } else if (GUI.Button(sprites.get(i).name, new Rect(0, i * 26, r.width, 26), empty, empty))
                    Editor.SetSelectedAsset(sprites.get(i));
            }
        } else if (selectedType == DataType.Texture) {
            List<Texture> textures = Texture.GetTextures();
            scroll2 = GUI.SetScrollView(textures.size() * 26, scroll2);
            for (i = 0; i < textures.size(); i++) {
                if (selected != null) {
                    if (textures.get(i).equals(selected)) {
                        if (GUI.Button(textures.get(i).Name(), new Rect(0, i * 26, r.width, 26), box, box)) {
                            if (Mouse.MultiClicked()) {
                                File f = new File(Editor.WorkingDirectory() + "Textures/" + textures.get(i).Name());
                                if (f.exists()) {
                                    try {
                                        Desktop.getDesktop().open(f);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Editor.SetSelectedAsset(textures.get(i));
                        }
                    } else if (GUI.Button(textures.get(i).Name(), new Rect(0, i * 26, r.width, 26), empty, empty))
                        Editor.SetSelectedAsset(textures.get(i));
                } else if (GUI.Button(textures.get(i).Name(), new Rect(0, i * 26, r.width, 26), empty, empty))
                    Editor.SetSelectedAsset(textures.get(i));
            }
        }
    }
}
