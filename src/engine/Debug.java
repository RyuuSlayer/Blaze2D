package engine;

import gui.GUI;

import java.util.ArrayList;
import java.util.List;

public class Debug {
    private static final List<String> log = new ArrayList<>();
    private static int i = 0;

    public static void Log(String message) {
        AddMessage(message);
    }

    private static void AddMessage(String message) {
        log.add(message);
        if (log.size() > 50) log.remove(0);
    }

    public static String Log() {
        StringBuilder ret = new StringBuilder();
        for (i = 0; i < log.size(); i++) ret.append(log.get(i)).append("\n");
        return ret.toString();
    }

    public static String LastEntry() {
        if (log.size() == 0) return "";
        return log.get(log.size() - 1);
    }

    public static void Draw() {
        if (GUI.Button(Debug.LastEntry(), new Rect(0, Application.Height() - 30, Application.Width(), 30), "Button", "ButtonHover")) {
            Dialog.MessageDialog("Debug Window", Log());
        }
    }
}