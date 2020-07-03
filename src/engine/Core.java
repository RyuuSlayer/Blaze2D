package engine;

import editor.Editor;
import editor.EditorUtil;
import editor.EngineBootLoader;
import gui.GUI;
import input.Input;
import input.Mouse;
import math.Color;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Core {
    public static void main(String[] args) {
        Editor.InitConfig();
        EngineBootLoader.Init();

        // Create the window
        long window = Application.Init();
        Color clear = Color.black;

        AssetDatabase.LoadAllResources();

        // Initialize the GUI
        GUI.Init();
        Editor.Init();
        Renderer.Init();
        Time.Init();

        SceneManager.LoadScene("New Scene");

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // While the application should not be closed
        while (!glfwWindowShouldClose(window)) {
            //At the start, poll events
            Mouse.Reset();
            Input.Reset();
            glfwPollEvents();
            Time.Process();

            if (!Application.IsMinimized()) {
                //Before drawing, clear what's been drawn previously and set the background color
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                Editor.Render();
                GameObject.PrepareObjects();

                if (Editor.IsPlaying())
                    Renderer.Render(new Rect(0, 30, Application.Width(), Application.Height() - 60));
                else Renderer.Render(new Rect(400, 30, Application.Width() - 800, Application.Height() - 260));

                glClearColor(clear.r, clear.g, clear.b, 1);

                GUI.Prepare();
                GUI.Label(Integer.toString(Time.FrameRate()), 10, 200);
                GUI.DrawPopup();
                GUI.Unbind();
            }

            // At the very end, swap the buffers
            glfwSwapBuffers(window);
        }
        // Cleanup the memory
        Texture.CleanUp();
        Mesh.CleanAllMesh();
        EditorUtil.CleanUp();

        // Destroy the window
        glfwTerminate();
    }
}