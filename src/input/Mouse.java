package input;

import engine.Application;
import engine.Time;
import math.Vector2;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

enum CursorImage {Pointer, Hand, HScroll, VScroll}

public class Mouse extends GLFWMouseButtonCallback {
    private static final byte[] buttons = new byte[8];
    private static final byte[] buttonsDown = new byte[8];
    private static final byte[] buttonsUp = new byte[8];
    private static byte anyButton = 0;
    private static byte anyButtonDown = 0;
    private static byte anyButtonUp = 0;
    private static long window;
    private static DoubleBuffer xBuffer;
    private static DoubleBuffer yBuffer;

    private static int scroll = 0;
    private static byte doubleClicked = 0;
    private static float lastDown = 0;

    public Mouse() {
        window = Application.Window();
        xBuffer = BufferUtils.createDoubleBuffer(1);
        yBuffer = BufferUtils.createDoubleBuffer(1);

        glfwSetScrollCallback(Application.Window(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scroll = (int) yoffset;
            }

        });
    }

    public static void Reset() {
        int i;
        for (i = 0; i < buttonsDown.length; i++) buttonsDown[i] = 0;
        for (i = 0; i < buttonsUp.length; i++) buttonsUp[i] = 0;
        anyButton = 0;
        anyButtonDown = 0;
        anyButtonUp = 0;

        scroll = 0;
        lastDown += Time.UnscaledDelta();
        doubleClicked = 0;
    }

    public static boolean MultiClicked() {
        return doubleClicked == 1;
    }

    public static boolean AnyButton() {
        return anyButton == 1;
    }

    public static boolean GetButton(int buttonCode) {
        return buttons[buttonCode] == 1;
    }

    public static boolean AnyButtonDown() {
        return anyButtonDown == 1;
    }

    public static boolean GetButtonDown(int buttonCode) {
        return buttonsDown[buttonCode] == 1;
    }

    public static boolean AnyButtonUp() {
        return anyButtonUp == 1;
    }

    public static boolean GetButtonUp(int buttonCode) {
        return buttonsUp[buttonCode] == 1;
    }

    public static Vector2 Position() {
        glfwGetCursorPos(window, xBuffer, yBuffer);
        return new Vector2((float) xBuffer.get(0), (float) yBuffer.get(0));
    }

    public static int Scroll() {
        return scroll;
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        anyButton = 1;
        if (action == GLFW_PRESS) {
            buttonsDown[button] = 1;
            anyButtonDown = 1;
            buttons[button] = 1;

            if (lastDown < 0.3f) doubleClicked = 1;
            lastDown = 0;
        }
        if (action == GLFW_RELEASE) {
            buttonsUp[button] = 1;
            anyButtonUp = 1;
            buttons[button] = 0;
        }
    }
}
