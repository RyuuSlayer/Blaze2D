package engine;

import gui.GUI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_messageBox;

public class Debug {
    private static final List<String> log = new ArrayList<String>();
    private static int i = 0;

    public static void Log(String message) {
        AddMessage(message);
    }

    private static void AddMessage(String message) {
        log.add(message);
        if (log.size() > 20) log.remove(0);
    }

    public static String Log() {
        String ret = "";
        for (i = 0; i < log.size(); i++) ret += (log.get(i) + "\n");
        return ret;
    }

    public static String LastEntry() {
        if (log.size() == 0) return "";
        return log.get(log.size() - 1);
    }

    public static void Draw() {
        if (GUI.Button(Debug.LastEntry(), new Rect(0, Application.Height() - 30, Application.Width(), 30), "Button", "ButtonHover")) {
            tinyfd_messageBox("Debug Log", Debug.Log(), "okcancel", "", true);
        }
    }
}
