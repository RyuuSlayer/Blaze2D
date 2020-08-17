package engine;

import gui.GUISkin;
import gui.Sprite;
import input.Input;
import input.Mouse;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Application {
    //Window specific variables
    public static String name = "Blaze2D Engine | Alpha Build";

    private static int width = 1200;
    private static int height = 600;
    private static long window;
    private static Rect r;
    private static byte minimized = 0;

    private static GLFWWindowSizeCallback windowSizeCallback;
    private static GLFWWindowFocusCallback windowFocusCallback;

    //Initialization function
    public static long Init() {
        //If glfw cannot be initialized
        if (!glfwInit()) {
            //Print an error to the console and close the application
            System.err.println("GLFW Failed to Initialize");
            System.exit(1);
        }

        r = new Rect(0, 0, width, height);

        //Create the window, show it and make the context current
        window = glfwCreateWindow(width, height, name, 0, 0);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);

        //Create capabilities and set the background color
        GL.createCapabilities();
        glClearColor(0, 0, 0, 1);

        glfwSetMouseButtonCallback(window, new Mouse());
        glfwSetKeyCallback(window, new Input());

        windowSizeCallback = GLFWWindowSizeCallback.create(Application::OnWindowResized);
        glfwSetWindowSizeCallback(window, windowSizeCallback);

        windowFocusCallback = GLFWWindowFocusCallback.create(Application::OnWindowChangedFocus);
        glfwSetWindowFocusCallback(window, windowFocusCallback);

        //Return the window
        return window;
    }

    private static void OnWindowChangedFocus(long win, boolean focused) {
        if (focused) {
            Sprite.RefreshAll();
            GUISkin.RefreshAll();
            Material.RefreshAll();
            Texture.RefreshAll();
            Shader.RefreshAll();
        }
    }

    public static void OnWindowResized(long win, int w, int h) {
        if (w == 0 && h == 0) minimized = 1;
        else minimized = 0;
        width = w;
        height = h;
        r.Set(0, 0, w, h);
        ProjectSettings.previousAppSize.Set(w, h);
        GL11.glViewport(0, 0, w, h);
        Renderer.UpdateFBO(w, h);
    }

    public static boolean IsMinimized() {
        return minimized == 1;
    }

    public static int Width() {
        return width;
    }

    public static int Height() {
        return height;
    }

    public static Rect GetRect() {
        return r;
    }

    public static long Window() {
        return window;
    }
}
