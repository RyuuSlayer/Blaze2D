package editor;

import engine.*;
import gui.Font;
import gui.*;
import input.Mouse;
import sound.AudioClip;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
            }
        }
    }

    public void Popup(String s) {
        if (s == null) return;

        if (s.equals("New Asset")) {
            if (selectedType == DataType.Shader) {
                try {
                    CreateShader();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot create shader asset!");
                }
            }
            if (selectedType == DataType.Material) {
                try {
                    CreateMaterial();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot create material asset!");
                }
            }
            if (selectedType == DataType.Skin) {
                try {
                    CreateSkin();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot create skin asset!");
                }
            }
            if (selectedType == DataType.Sprite) {
                try {
                    CreateSprite();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot create sprite asset!");
                }
            }
            if (selectedType == DataType.Script) {
                try {
                    CreateScript();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.Log("Cannot create script!");
                }
            }
        }
    }

    private void RenderAssetType(Rect r, engine.Object asset, String folder, String ext, byte containsMouse) throws CloneNotSupportedException {
        if (selected != null) {
            if (asset.instanceID().equals(selected.instanceID())) {
                if (GUI.Button(asset.Name(), r, box, box)) {
                    if (Mouse.MultiClicked()) {
                        if (selectedType == DataType.Scene) {
                            SceneManager.LoadScene(asset.Name());
                        } else {
                            File f = new File(Editor.WorkingDirectory() + folder + asset.Name() + ext);
                            if (f.exists()) {
                                try {
                                    Desktop.getDesktop().open(f);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
            List<Material> altered = new ArrayList<Material>();
            for (i = 0; i < materials.size(); i++) {
                if (!materials.get(i).isInternal()) altered.add(materials.get(i));
            }

            scroll2 = GUI.SetScrollView(altered.size() * 26, scroll2);
            for (i = 0; i < altered.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), altered.get(i), "Materials/", ".Material", containsMouse);
            }
        } else if (selectedType == DataType.Shader) {
            List<Shader> shaders = Shader.Shaders();
            List<Shader> altered = new ArrayList<Shader>();
            for (i = 0; i < shaders.size(); i++) {
                if (!shaders.get(i).isInternal()) altered.add(shaders.get(i));
            }

            scroll2 = GUI.SetScrollView(altered.size() * 26, scroll2);
            for (i = 0; i < altered.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), altered.get(i), "Shaders/", ".Shader", containsMouse);
            }
        } else if (selectedType == DataType.Skin) {
            List<GUISkin> skins = GUISkin.Skins();
            List<GUISkin> altered = new ArrayList<GUISkin>();
            for (i = 0; i < skins.size(); i++) {
                if (!skins.get(i).isInternal()) altered.add(skins.get(i));
            }

            scroll2 = GUI.SetScrollView(altered.size() * 26, scroll2);
            for (i = 0; i < altered.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), skins.get(i), "Skins/", ".Skin", containsMouse);
            }
        } else if (selectedType == DataType.Sprite) {
            List<Sprite> sprites = Sprite.Sprites();
            List<Sprite> altered = new ArrayList<Sprite>();
            for (i = 0; i < sprites.size(); i++) {
                if (!sprites.get(i).isInternal()) altered.add(sprites.get(i));
            }

            scroll2 = GUI.SetScrollView(altered.size() * 26, scroll2);
            for (i = 0; i < altered.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), altered.get(i), "Sprites/", ".Sprite", containsMouse);
            }
        } else if (selectedType == DataType.Texture) {
            List<Texture> textures = Texture.GetTextures();
            List<Texture> altered = new ArrayList<Texture>();
            for (i = 0; i < textures.size(); i++) {
                if (!textures.get(i).isInternal()) altered.add(textures.get(i));
            }

            scroll2 = GUI.SetScrollView(altered.size() * 26, scroll2);
            for (i = 0; i < altered.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), altered.get(i), "Textures/", "", containsMouse);
            }
        } else if (selectedType == DataType.AudioClip) {
            List<AudioClip> clips = AudioClip.GetClips();
            scroll2 = GUI.SetScrollView(clips.size() * 26, scroll2);
            for (i = 0; i < clips.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), clips.get(i), "Audio/", "", containsMouse);
            }
        } else if (selectedType == DataType.Script) {
            List<LogicBehaviour> scripts = EditorUtil.GetImportedClasses();
            scroll2 = GUI.SetScrollView(scripts.size() * 26, scroll2);
            for (i = 0; i < scripts.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), scripts.get(i), "Scripts/", "", containsMouse);
            }
        } else if (selectedType == DataType.Scene) {
            List<Scene> scenes = Scene.GetScenes();
            scroll2 = GUI.SetScrollView(scenes.size() * 26, scroll2);
            for (i = 0; i < scenes.size(); i++) {
                RenderAssetType(new Rect(0, i * 26, r.width, 26), scenes.get(i), "Scenes/", "", containsMouse);
            }
        }
    }

    public void CreateSprite() throws IOException {
        int n = -1;
        File f = new File(Editor.WorkingDirectory() + "Sprites/NewSprite.Sprite");
        if (!f.exists()) {
            f.createNewFile();
            n = 0;
        } else {
            for (n = 1; n < 30; n++) {
                f = new File(Editor.WorkingDirectory() + "Sprites/NewSprite_" + n + ".Sprite");
                if (!f.exists()) {
                    f.createNewFile();
                    break;
                } else if (n == 30) n = -1;
            }
        }
        if (n == -1) {
            Debug.Log("The name NewSprite can only have up to 30 entries. Please rename 1 more of the current sprites. File not created");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("Material: NULL\r\nOffset: 0,0,16,16\r\npadding: 0,0,0,0");
        bw.close();

        new Sprite(f.getAbsolutePath().split("\\.")[0]);
    }

    public void CreateMaterial() throws IOException {
        int n = -1;
        File f = new File(Editor.WorkingDirectory() + "Materials/NewMaterial.Material");
        if (!f.exists()) {
            f.createNewFile();
            n = 0;
        } else {
            for (n = 1; n < 30; n++) {
                f = new File(Editor.WorkingDirectory() + "Materials/NewMaterial_" + n + ".Material");
                if (!f.exists()) {
                    f.createNewFile();
                    break;
                } else if (n == 30) n = -1;
            }
        }
        if (n == -1) {
            Debug.Log("The name NewMaterial can only have up to 30 entries. Please rename 1 more of the current materials. File not created");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("Texture: NULL\r\nColor: 1,1,1,1\r\nShader: NULL");
        bw.close();

        new Material(f.getAbsolutePath().split("\\.")[0]);
    }

    public void CreateShader() throws IOException {
        int n = -1;
        File f = new File(Editor.WorkingDirectory() + "Shaders/NewShader.Shader");
        if (!f.exists()) {
            f.createNewFile();
            n = 0;
        } else {
            for (n = 1; n < 30; n++) {
                f = new File(Editor.WorkingDirectory() + "Shaders/NewShader_" + n + ".Shader");
                if (!f.exists()) {
                    f.createNewFile();
                    break;
                } else if (n == 30) n = -1;
            }
        }
        if (n == -1) {
            Debug.Log("The name NewShader can only have up to 30 entries. Please rename 1 more of the current shaders. File not created");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        bw.write("#version 140\r\n" + "\r\n" + "in vec2 position;\r\n" + "in vec2 textureCoords;\r\n" + "\r\n" + "out vec4 color;\r\n" + "out vec2 uvCoords;\r\n" + "\r\n" +
                "uniform vec4 matColor;\r\n" + "uniform mat4 projection;\r\n" + "uniform vec4 offset;\r\n" + "uniform vec2 pixelScale;\r\n" + "uniform vec2 screenPos;\r\n" + "uniform mat4 transformationMatrix;\r\n" + "uniform vec2 anchor;\r\n" + "uniform vec2 camPos;\r\n" + "uniform float depth;\r\n" + "\r\n" +
                "void main()\r\n" + "{\r\n" + "	color = matColor;\r\n" + "	\r\n" + "	vec4 worldPosition = vec4((position - anchor) * pixelScale, depth, 1) * transformationMatrix;\r\n" + "	gl_Position = projection * (worldPosition - vec4(camPos, 0, 0));\r\n" + "	uvCoords = (textureCoords * offset.zw) + offset.xy;\r\n" + "}\r\n" +
                "\r\n" + "ENDVERTEX\r\n" + "\r\n" +
                "#version 140\r\n" + "\r\n" +
                "uniform sampler2D sampler;\r\n" + "\r\n" + "in vec4 color;\r\n" + "in vec2 uvCoords;\r\n" + "\r\n" +
                "void main()\r\n" + "{\r\n" + "	gl_FragColor = color * texture2D(sampler, uvCoords);\r\n" + "}");

        bw.close();

        new Shader(f.getAbsolutePath().split("\\.")[0]);
    }

    public void CreateSkin() throws IOException {
        int n = -1;
        File f = new File(Editor.WorkingDirectory() + "Skins/NewSkin.Skin");
        if (!f.exists()) {
            f.createNewFile();
            n = 0;
        } else {
            for (n = 1; n < 30; n++) {
                f = new File(Editor.WorkingDirectory() + "Skins/NewSkin_" + n + ".Skin");
                if (!f.exists()) {
                    f.createNewFile();
                    break;
                } else if (n == 30) n = -1;
            }
        }
        if (n == -1) {
            Debug.Log("The name NewSkin can only have up to 30 entries. Please rename 1 more of the current skins. File not created");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("image: DefaultGUI\r\nName: Default\r\nOffset: 0,0,16,16\r\nPadding: 0,0,0,0");
        bw.close();

        new GUISkin(f.getAbsolutePath().split("\\.")[0]);
    }

    public void CreateScript() throws IOException {
        int n = -1;
        File f = new File(Editor.WorkingDirectory() + "Scripts/NewScript.java");
        if (!f.exists()) {
            f.createNewFile();
            n = 0;
        } else {
            for (n = 1; n < 30; n++) {
                f = new File(Editor.WorkingDirectory() + "Scripts/NewScript_" + n + ".java");
                if (!f.exists()) {
                    f.createNewFile();
                    break;
                } else if (n == 30) n = -1;
            }
        }
        if (n == -1) {
            Debug.Log("The name NewScript can only have up to 30 entries. Please rename 1 more of the current scripts. File not created");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        if (n == 0)
            bw.write("import engine.LogicBehaviour;\r\n\r\npublic class NewScript extends LogicBehaviour\r\n{\r\n\tpublic void Update()\r\n\t{\r\n\t\t\r\n\t}\r\n}");
        else
            bw.write("import engine.LogicBehaviour;\r\n\r\npublic class NewScript_" + n + " extends LogicBehaviour\r\n{\r\n\tpublic void Update()\r\n\t{\r\n\t\t\r\n\t}\r\n}");
        bw.close();

        //We can now do this, had to pass in absolute path because workingdirectory uses \ while scripts is use /
        EditorUtil.ImportClass(f.getAbsolutePath());
    }
}
